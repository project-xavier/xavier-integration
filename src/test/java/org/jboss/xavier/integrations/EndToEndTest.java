package org.jboss.xavier.integrations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.api.SoftAssertions;
import org.awaitility.Duration;
import org.jboss.xavier.Application;
import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.analytics.pojo.output.workload.summary.WorkloadSummaryReportModel;
import org.jboss.xavier.integrations.jpa.repository.AnalysisRepository;
import org.jboss.xavier.integrations.jpa.repository.InitialSavingsEstimationReportRepository;
import org.jboss.xavier.integrations.jpa.service.InitialSavingsEstimationReportService;
import org.jboss.xavier.integrations.route.model.user.User;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.BinaryBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.awaitility.Awaitility.await;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;


@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = EndToEndTest.Initializer.class)
@Import(TestConfigurationS3.class)
@ActiveProfiles("test")
public class EndToEndTest {

    @ClassRule
    public static GenericContainer activemq = new GenericContainer<>("vromero/activemq-artemis")
            .withExposedPorts(61616)
            .withEnv("DISABLE_SECURITY", "true")
            .withEnv("BROKER_CONFIG_GLOBAL_MAX_SIZE", "50000")
            .withEnv("BROKER_CONFIG_MAX_SIZE_BYTES", "50000")
            .withEnv("BROKER_CONFIG_MAX_DISK_USAGE", "100")
            ;

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer()
            .withEnv("KAFKA_CREATE_TOPICS", "platform.upload.xavier")
            .withExposedPorts(9092, 9093);

    @ClassRule
    public static PostgreSQLContainer postgreSQL = new PostgreSQLContainer()
            .withDatabaseName("sampledb")
            .withUsername("admin")
            .withPassword("redhat");

    @ClassRule
    public static LocalStackContainer localstack = new LocalStackContainer()
            .withServices(S3);

    @Inject
    private InitialSavingsEstimationReportService initialSavingsEstimationReportService;

    @Autowired
    InitialSavingsEstimationReportRepository initialSavingsEstimationReportRepository;

    @Inject
    private AnalysisRepository analysisRepository;

    @Value("${S3_BUCKET}")
    private String bucket;

    @Value("${performancetest.timeout:5000")
    private Long timeout;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            EnvironmentTestUtils.addEnvironment("environment", configurableApplicationContext.getEnvironment(),
                    "spring.activemq.broker-url=" + activemq.getContainerIpAddress() + ":" + activemq.getMappedPort(61616),
                    "spring.artemis.host="+ activemq.getContainerIpAddress(),
                    "spring.artemis.port="+ activemq.getMappedPort(61616),
                    "amq.server=" + activemq.getContainerIpAddress(),
                    "amq.port=" + activemq.getMappedPort(61616),
                    "insights.upload.host=localhost:8000",
                    "camel.component.servlet.mapping.context-path=/api/xavier/*",
                    "insights.kafka.host=" + kafka.getBootstrapServers(),
                    "postgresql.service.name=" + postgreSQL.getContainerIpAddress(),
                    "postgresql.service.port=" + postgreSQL.getFirstMappedPort(),
                    "spring.datasource.username=" + postgreSQL.getUsername(),
                    "spring.datasource.password=" + postgreSQL.getPassword(),
                    "S3_HOST=" + localstack.getEndpointConfiguration(S3).getServiceEndpoint(),
                    "S3_REGION="+ localstack.getEndpointConfiguration(S3).getSigningRegion(),
                    "kieserver.devel-service=localhost:8000");
        }
    }

    @Inject
    CamelContext camelContext;

    @Inject
    JmsTemplate jmsTemplate;

    @Inject
    AmazonS3 amazonS3;

    private static ClientAndServer clientAndServer;



    @Before
    public void setupMockServerFor_InsightsUpload_KIEServer() {

        clientAndServer = ClientAndServer.startClientAndServer(8000);

        clientAndServer.when(request()
                .withPath("/api/ingress/v1/upload"))
                .respond(myrequest -> {
                    try {
                        sendKafkaMessageToSimulateInsightsUploadProcess();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return response().withStatusCode(200).withBody("success").withHeader("Content-Type", "application/zip");
                });

        clientAndServer.when(request()
                                .withPath("/insights-upload-perm-test/440c88f9-5930-416e-9799-fa01d156df29"))
                        .respond(myrequest -> {
                            try {
                                BinaryBody body = new BinaryBody(IOUtils.resourceToByteArray("cfme_inventory-20190912-demolab_withSSA.tar.gz", EndToEndTest.class.getClassLoader()));
                                return response()
                                        .withHeader("Content-Type", "application/zip")
                                        .withHeader("Accept-Ranges", "bytes")
                                        .withHeader("Content-Length", Integer.toString(body.getRawBytes().length))
                                        .withBody(body);
                            } catch (IOException e) {
                                return notFoundResponse();
                            }
                        });

        //  http://myapp-kieserver-rhdm73-authoring.127.0.0.1.nip.io:80/services/rest/server/containers/instances/xavier-analytics_0.0.1-SNAPSHOT?authUsername=executionUser&authMethod=Basic&authPassword=xxxxxx
        clientAndServer.when(request()
                                .withPath("/services/rest/server/containers/instances/xavier-analytics_0.0.1-SNAPSHOT"))
                        .respond(myrequest -> {
                            try {
                                String resourceName;

                                if (myrequest.getBodyAsString().toLowerCase().contains("workloadinventory")) {
                                    resourceName = "kie-server-response-workloadinventoryreport.xml";
                                } else {
                                    resourceName = "kie-server-response-initialcostsavingsreport.xml";
                                }
                                return response()
                                        .withHeader("Content-Type", "text/xml")
                                        .withStatusCode(200)
                                        .withBody(IOUtils.resourceToString(resourceName, StandardCharsets.UTF_8, EndToEndTest.class.getClassLoader()));
                            } catch (Exception e) {
                                e.printStackTrace();
                                return notFoundResponse();
                            }
                        });
    }

    @AfterClass
    public static void closeMockServer() {
        clientAndServer.stop();
    }

    private void sendKafkaMessageToSimulateInsightsUploadProcess() throws ExecutionException, InterruptedException, IOException {
        String body = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("platform.upload.xavier-with-targz.json"), Charset.forName("UTF-8"));
        body = body.replaceAll("http://172.17.0.1:9000", "http://localhost:8000");

        final ProducerRecord<String, String> record = new ProducerRecord<>("platform.upload.xavier", body );

        RecordMetadata metadata = createKafkaProducer().send(record).get();
        System.out.println("Kafka answer : " + metadata);
    }

    private static Producer<String, String> createKafkaProducer() {
        KafkaProducer<String, String> producer = new KafkaProducer<>(
                ImmutableMap.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString()
                ),
                new StringSerializer(),
                new StringSerializer()
        );

        return producer;
    }

    @Test
    public void end2endTest() throws Exception {
        // given
        camelContext.setTracing(true);
        camelContext.start();

        camelContext.getRouteDefinition("store-in-s3").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() {
                weaveById("set-s3-key")
                        .replace().process(e -> e.getIn().setHeader(S3Constants.KEY, "S3KEY123"));
            }
        });

        // 1. Check user has firstTime
        ResponseEntity<User> userEntity = new RestTemplate().exchange("http://localhost:8080/api/xavier/user", HttpMethod.GET, getRequestEntity(), new ParameterizedTypeReference<User>() {});
        assertThat(userEntity.getBody().isFirstTimeCreatingReports()).isTrue();

        // Start the camel route as if the UI was sending the file to the Camel Rest Upload route
        new RestTemplate().postForEntity("http://localhost:8080/api/xavier/upload", getRequestEntityForUploadRESTCall("cfme_inventory-20190912-demolab_withSSA.tar.gz"), String.class);

        // then
        Thread.sleep(10000); //TODO check another approach


        // Check database
        assertThat(initialSavingsEstimationReportRepository.findAll()).isNotNull().isNotEmpty();

        // Check S3
        S3Object s3object = amazonS3.getObject(bucket, "S3KEY123");
        assertThat(s3object.getObjectContent()).isNotNull();
        assertThatExceptionOfType(AmazonS3Exception.class).isThrownBy(() -> amazonS3.getObject(bucket, "NONEXISTINGFILE"));

        // Check DB for initialCostSavingsReport
        InitialSavingsEstimationReportModel initialCostSavingsReportDB = initialSavingsEstimationReportService.findByAnalysisOwnerAndAnalysisId("mrizzi@redhat.com", 1L);
        assertThat(initialCostSavingsReportDB.getEnvironmentModel().getHypervisors() == 2);
        assertThat(initialCostSavingsReportDB.getSourceCostsModel().getYear1Server() == 42);

        // Call initialCostSavingsReport
        ResponseEntity<InitialSavingsEstimationReportModel> initialCostSavingsReport = new RestTemplate().exchange("http://localhost:8080/api/xavier/report/1/initial-saving-estimation", HttpMethod.GET, getRequestEntity(), new ParameterizedTypeReference<InitialSavingsEstimationReportModel>() {});

        // Call workloadInventoryReport
        ResponseEntity<PagedResources<WorkloadInventoryReportModel>> workloadInventoryReport = new RestTemplate().exchange("http://localhost:8080/api/xavier/report/1/workload-inventory?size=100", HttpMethod.GET, getRequestEntity(), new ParameterizedTypeReference<PagedResources<WorkloadInventoryReportModel>>() {});

        // Call workloadSummaryReport
        ResponseEntity<WorkloadSummaryReportModel> workloadSummaryReport = new RestTemplate().exchange("http://localhost:8080/api/xavier/report/1/workload-summary", HttpMethod.GET, getRequestEntity(), new ParameterizedTypeReference<WorkloadSummaryReportModel>() {});

        // Checks on Initial Savings Report
        InitialSavingsEstimationReportModel initialSavingsEstimationReport_Expected = new ObjectMapper().readValue(IOUtils.resourceToString("cfme_inventory-20190912-demolab-withssa-initial-cost-savings-report.json", StandardCharsets.UTF_8, EndToEndTest.class.getClassLoader()), InitialSavingsEstimationReportModel.class);
        SoftAssertions.assertSoftly(softly -> softly.assertThat(initialSavingsEstimationReport_Expected).isEqualToComparingFieldByField(initialCostSavingsReport.getBody()));

        // Checks on Workload Summary Report
        WorkloadSummaryReportModel workloadSummaryReport_Expected = new ObjectMapper().readValue(IOUtils.resourceToString("cfme_inventory-20190912-demolab-withssa-workload-summary-report.json", StandardCharsets.UTF_8, EndToEndTest.class.getClassLoader()), WorkloadSummaryReportModel.class);
        SoftAssertions.assertSoftly(softly -> softly.assertThat(workloadSummaryReport_Expected).isEqualToComparingFieldByField(workloadSummaryReport.getBody()));

        // Checks on Workload Inventory Report
        WorkloadInventoryReportModel workloadInventoryReport_Expected = new ObjectMapper().readValue(IOUtils.resourceToString("cfme_inventory-20190912-demolab-withssa-workload-inventory-report.json", StandardCharsets.UTF_8, EndToEndTest.class.getClassLoader()), WorkloadInventoryReportModel.class);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(workloadInventoryReport_Expected).isEqualToComparingFieldByField(workloadInventoryReport.getBody());

            softly.assertThat(workloadInventoryReport.getBody().getContent().size()).isEqualTo(14); // OK
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().map(WorkloadInventoryReportModel::getWorkloads).distinct().count()).isEqualTo(7);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().filter(e -> e.getWorkloads().contains("Red Hat JBoss EAP")).count()).isEqualTo(2);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().map(WorkloadInventoryReportModel::getOsName).distinct().count()).isEqualTo(4);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().filter(e -> e.getOsName().contains("CentoS 7 (64-bit)")).count()).isEqualTo(2);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().map(WorkloadInventoryReportModel::getComplexity).distinct().count()).isEqualTo(3);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().filter(e -> e.getComplexity().contains("Unknown")).count()).isEqualTo(1);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().flatMap(e -> e.getRecommendedTargetsIMS().stream()).distinct().count()).isEqualTo(3);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().filter(e -> e.getRecommendedTargetsIMS().contains("OSP")).count()).isEqualTo(13);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().map(e -> e.getFlagsIMS().stream()).distinct().count()).isEqualTo(2);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().filter(e -> e.getFlagsIMS().contains("Shared Disk")).count()).isEqualTo(2);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().filter(e -> e.getOsName().contains("SernerNT") && e.getWorkloads().contains("Microsoft SQL Server")).count()).isEqualTo(1);
        });

        // Performance test
        new RestTemplate().postForEntity("http://localhost:8080/api/xavier/upload", getRequestEntityForUploadRESTCall("cfme_inventory-20190829-16128-uq17dx.tar.gz"), String.class);
        ResponseEntity<WorkloadSummaryReportModel> workloadSummaryReport_PerformanceTest = new RestTemplate().exchange("http://localhost:8080/api/xavier/report/1/workload-summary", HttpMethod.GET, getRequestEntity(), new ParameterizedTypeReference<WorkloadSummaryReportModel>() {});
        await()
            .atMost(timeout, TimeUnit.MILLISECONDS)
            .with().pollInterval(Duration.ONE_HUNDRED_MILLISECONDS)
            .until(() -> {
                ResponseEntity<String> answer = new RestTemplate().postForEntity("http://localhost:8080/api/xavier/upload", getRequestEntityForUploadRESTCall("cfme_inventory-20190829-16128-uq17dx.tar.gz"), String.class);
                return (answer != null && answer.getStatusCodeValue() == 200);
             });

        camelContext.stop();
    }

    private HttpEntity getRequestEntity() {
        return new HttpEntity<String>(getHttpHeaders());
    }

    @NotNull
    private HttpEntity<MultiValueMap<String, Object>> getRequestEntityForUploadRESTCall(String filename) throws IOException {
        // Headers
        HttpHeaders headers = getHttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // File Body part
        //String filename = "cloudforms-export-v1_0_0-multiple-files.tar.gz";
        LinkedMultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=filex; filename=" + filename);
        fileMap.add("Content-type", "application/zip");
        body.add("file", new HttpEntity<>(IOUtils.resourceToByteArray(filename, EndToEndTest.class.getClassLoader()), fileMap));

        // params Body parts
        body.add("dummy", "CID12345");
        body.add("year1hypervisorpercentage", "50");
        body.add("year2hypervisorpercentage", "25");
        body.add("year3hypervisorpercentage", "25");
        body.add("growthratepercentage", "5");
        body.add("sourceproductindicator", "2");
        body.add("reportName", "report name test");
        body.add("reportDescription", "report desc test");
        body.add("payloadName", "payloadname");


        return new HttpEntity<>(body, headers);
    }

    @NotNull
    private HttpHeaders getHttpHeaders() {
        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rh-insights-request-id", "2544925e825b4f3f9418c88556541776");
        headers.set("x-rh-identity", "eyJlbnRpdGxlbWVudHMiOnsiaW5zaWdodHMiOnsiaXNfZW50aXRsZWQiOnRydWV9LCJvcGVuc2hpZnQiOnsiaXNfZW50aXRsZWQiOnRydWV9LCJzbWFydF9tYW5hZ2VtZW50Ijp7ImlzX2VudGl0bGVkIjpmYWxzZX0sImh5YnJpZF9jbG91ZCI6eyJpc19lbnRpdGxlZCI6dHJ1ZX19LCJpZGVudGl0eSI6eyJpbnRlcm5hbCI6eyJhdXRoX3RpbWUiOjAsImF1dGhfdHlwZSI6Imp3dC1hdXRoIiwib3JnX2lkIjoiNjM0MDA1NiIsICJmaWxlbmFtZSI6ImNsb3VkZm9ybXMtZXhwb3J0LXYxXzBfMC1tdWx0aXBsZS1maWxlcy50YXIuZ3oiLCJvcmlnaW4iOiJtYS14YXZpZXIiLCJjdXN0b21lcmlkIjoiQ0lEODg4IiwgImFuYWx5c2lzSWQiOiIxIn0sImFjY291bnRfbnVtYmVyIjoiMTQ2MDI5MCIsICJ1c2VyIjp7ImZpcnN0X25hbWUiOiJNYXJjbyIsImlzX2FjdGl2ZSI6dHJ1ZSwiaXNfaW50ZXJuYWwiOnRydWUsImxhc3RfbmFtZSI6IlJpenppIiwibG9jYWxlIjoiZW5fVVMiLCJpc19vcmdfYWRtaW4iOmZhbHNlLCJ1c2VybmFtZSI6Im1yaXp6aUByZWRoYXQuY29tIiwiZW1haWwiOiJtcml6emkrcWFAcmVkaGF0LmNvbSJ9LCJ0eXBlIjoiVXNlciJ9fQ==");
        headers.set("username", "mrizzi@redhat.com");
        return headers;
    }
}

package org.jboss.xavier.integrations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.awaitility.Awaitility.await;
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
            .withExposedPorts(61616, 8161)
            .withEnv("DISABLE_SECURITY", "true")
            .withEnv("BROKER_CONFIG_GLOBAL_MAX_SIZE", "50000")
            .withEnv("BROKER_CONFIG_MAX_SIZE_BYTES", "50000")
            .withEnv("BROKER_CONFIG_MAX_DISK_USAGE", "100");

    @ClassRule
    public static GenericContainer kie_server = new GenericContainer<>("jboss/kie-server-showcase:7.18.0.Final")
            .withNetworkAliases("kie-server")
            .withExposedPorts(8080)
            .withEnv("KIE_SERVER_ID", "analytics-kieserver")
            .withEnv("KIE_ADMIN_USER", "kieserver")
            .withEnv("KIE_ADMIN_PWD", "kieserver1!")
            .withEnv("KIE_SERVER_MODE", "DEVELOPMENT")
            .withEnv("KIE_MAVEN_REPO", "https://oss.sonatype.org/content/repositories/snapshots")
            .withEnv("KIE_REPOSITORY","https://repository.jboss.org/nexus/content/groups/public-jboss")
            .withEnv("KIE_SERVER_CONTROLLER_PWD","admin")
            .withEnv("KIE_SERVER_CONTROLLER_USER","admin")
            .withEnv("KIE_SERVER_LOCATION","http://kie-server:8080/kie-server/services/rest/server")
            .withEnv("KIE_SERVER_PWD","kieserver1!")
            .withEnv("KIE_SERVER_USER","kieserver");

    @ClassRule
    public static PostgreSQLContainer postgreSQL = new PostgreSQLContainer()
            .withDatabaseName("sampledb")
            .withUsername("admin")
            .withPassword("redhat");

    @ClassRule
    public static DockerComposeContainer ingressCompose = getDockerComposeContainerForIngress()
                    .withExposedService("kafka", 29092)
                    .withExposedService("ingress", 3000)
                    .withExposedService("minio", 9000 );

    @ClassRule
    public static LocalStackContainer localstack = new LocalStackContainer().withServices(S3);

    @NotNull
    private static DockerComposeContainer getDockerComposeContainerForIngress() {
        String dockerComposeFilePath = "src/test/resources/insights-ingress-go-7988e163ff4e65edf5585e35d1774181f5ad92e9/docker-compose.yml";
        try {
            cloneIngressRepoAndUnzip();
            // TODO we need to comment the line "image: ingress:latest"
            replaceImageOnFile(dockerComposeFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File dockerComposeFile = new File(dockerComposeFilePath);
        return new DockerComposeContainer(dockerComposeFile).withLocalCompose(true);
    }

    private static void replaceImageOnFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("image: ingress:latest", "#image: ingress:latest");
        content = content.replaceAll("- INGRESS_VALIDTOPICS=", "- INGRESS_VALIDTOPICS=xavier,testareno,advisor");
        Files.write(path, content.getBytes(charset));
    }

    @NotNull
    private static DockerComposeContainer getDockerComposeContainerForDroolsKie() {
        File file = new File(EndToEndTest.class.getClassLoader().getResource("drools-kie-compose.yml").getFile());
        return new DockerComposeContainer(file).withLocalCompose(true);
    }

    private static String serverInstanceId;

    @Inject
    private InitialSavingsEstimationReportService initialSavingsEstimationReportService;

    @Autowired
    InitialSavingsEstimationReportRepository initialSavingsEstimationReportRepository;

    @Inject
    private AnalysisRepository analysisRepository;

    @Value("${S3_BUCKET}")
    private String bucket;
    @Value("${performancetest.timeout:60000}")
    private Long timeout;

    @Value("${server.port:8080}")
    private String serverPort;

    private static final String analyticsArtifact = "xavier-analytics";
    private static final String analyticsVersion = "0.0.1-SNAPSHOT";

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            try {
                importProjectIntoDrools();
                EnvironmentTestUtils.addEnvironment("environment", configurableApplicationContext.getEnvironment(),
                        "amq.server=" + activemq.getContainerIpAddress(),
                        "amq.port=" + activemq.getMappedPort(61616),
                        "insights.upload.host=" + getHostAndPortFromIngressComposeForService("ingress", 3000),
                        "camel.component.servlet.mapping.context-path=/api/xavier/*",
                        "insights.kafka.host=" + getHostAndPortFromIngressComposeForService("kafka", 29092),
                        "postgresql.service.name=" + postgreSQL.getContainerIpAddress(),
                        "postgresql.service.port=" + postgreSQL.getFirstMappedPort(),
                        "spring.datasource.username=" + postgreSQL.getUsername(),
                        "spring.datasource.password=" + postgreSQL.getPassword(),
                        "S3_HOST=" + localstack.getEndpointConfiguration(S3).getServiceEndpoint(),
                        "S3_REGION="+ localstack.getEndpointConfiguration(S3).getSigningRegion(),
                        "kieserver.devel-service=" + getHostForKie() + "/kie-server",
                        "spring.datasource.url = jdbc:postgresql://" + postgreSQL.getContainerIpAddress() + ":" + postgreSQL.getFirstMappedPort() + "/sampledb" ,
                        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL9Dialect");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getHostAndPortFromIngressComposeForService(String service, Integer port) {
            return ingressCompose.getServiceHost(service, port) + ":" + ingressCompose.getServicePort(service, port);
        }
    }

    @Inject
    CamelContext camelContext;

    @Inject
    JmsTemplate jmsTemplate;

    @Inject
    AmazonS3 amazonS3;

    private static void cloneIngressRepoAndUnzip() throws IOException {
        String ingressRepoZipURL = "https://github.com/RedHatInsights/insights-ingress-go/archive/7988e163ff4e65edf5585e35d1774181f5ad92e9.zip";
        File destination = new File("src/test/resources/ingressRepo.zip");
        FileUtils.copyURLToFile(new URL(ingressRepoZipURL), destination, 1000, 10000);
        unzipFile(destination, "src/test/resources");
    }

    private static void unzipFile(File file, String outputDir) throws IOException {
        java.util.zip.ZipFile zipFile = new ZipFile(file);
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    InputStream in = zipFile.getInputStream(entry);
                    OutputStream out = new FileOutputStream(entryDestination);
                    IOUtils.copy(in, out);
                    in.close();
                    out.close();
                }
            }
        } finally {
            zipFile.close();
        }
    }

    private static String getHostForKie() {
        return kie_server.getContainerIpAddress() + ":" + kie_server.getFirstMappedPort();
    }

    private static void importProjectIntoDrools() throws InterruptedException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");
        headers.set("Authorization", "Basic YWRtaW46YWRtaW4="); // admin:admin

        String kieRestURL = "http://" + getHostForKie() + "/kie-server/services/rest/";

        // KIE Container Creation
        HttpHeaders kieheaders = new HttpHeaders();
        kieheaders.setContentType(MediaType.APPLICATION_JSON);
        kieheaders.set("Authorization", "Basic a2llc2VydmVyOmtpZXNlcnZlcjEh");
        kieheaders.setCacheControl("no-cache");
        String kieContainerBody = "{\"container-id\" : \"xavier-analytics_0.0.1-SNAPSHOT\",\"release-id\" : {\"group-id\" : \"org.jboss.xavier\",\"artifact-id\" : \"xavier-analytics\",\"version\" : \"0.0.1-SNAPSHOT\" } }";
        try {
            ResponseEntity<String> responseCreateKIEContainer = new RestTemplate().exchange(kieRestURL + "server/containers/xavier-analytics_0.0.1-SNAPSHOT", HttpMethod.PUT, new HttpEntity<>(kieContainerBody, kieheaders), String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        serverInstanceId = analyticsArtifact + "_" + analyticsVersion;
    }

    @Test
    public void end2endTest() throws Exception {
        // given
        camelContext.setTracing(true);
        camelContext.getGlobalOptions().put(Exchange.LOG_DEBUG_BODY_MAX_CHARS, "5000");
        camelContext.start();

        camelContext.getRouteDefinition("store-in-s3").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() {
                weaveById("set-s3-key")
                        .replace().process(e -> e.getIn().setHeader(S3Constants.KEY, "S3KEY123"));
            }
        });

        // 1. Check user has firstTime
        ResponseEntity<User> userEntity = new RestTemplate().exchange("http://localhost:" + serverPort + "/api/xavier/user", HttpMethod.GET, getRequestEntity(), new ParameterizedTypeReference<User>() {});
        assertThat(userEntity.getBody().isFirstTimeCreatingReports()).isTrue();

        // Start the camel route as if the UI was sending the file to the Camel Rest Upload route
        new RestTemplate().postForEntity("http://localhost:" + serverPort + "/api/xavier/upload", getRequestEntityForUploadRESTCall("cfme_inventory-20190912-demolab_withSSA.tar.gz"), String.class);

        // then
        Thread.sleep(120000); //TODO check another approach

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
        ResponseEntity<InitialSavingsEstimationReportModel> initialCostSavingsReport = new RestTemplate().exchange("http://localhost:" + serverPort + "/api/xavier/report/1/initial-saving-estimation", HttpMethod.GET, getRequestEntity(), new ParameterizedTypeReference<InitialSavingsEstimationReportModel>() {});

        // Call workloadInventoryReport
        ResponseEntity<PagedResources<WorkloadInventoryReportModel>> workloadInventoryReport = new RestTemplate().exchange("http://localhost:" + serverPort + "/api/xavier/report/1/workload-inventory?size=100", HttpMethod.GET, getRequestEntity(), new ParameterizedTypeReference<PagedResources<WorkloadInventoryReportModel>>() {});

        // Call workloadSummaryReport
        ResponseEntity<WorkloadSummaryReportModel> workloadSummaryReport = new RestTemplate().exchange("http://localhost:" + serverPort + "/api/xavier/report/1/workload-summary", HttpMethod.GET, getRequestEntity(), new ParameterizedTypeReference<WorkloadSummaryReportModel>() {});

        // Checks on Initial Savings Report
        InitialSavingsEstimationReportModel initialSavingsEstimationReport_Expected = new ObjectMapper().readValue(IOUtils.resourceToString("cfme_inventory-20190912-demolab-withssa-initial-cost-savings-report.json", StandardCharsets.UTF_8, EndToEndTest.class.getClassLoader()), InitialSavingsEstimationReportModel.class);
        SoftAssertions.assertSoftly(softly -> softly.assertThat(initialSavingsEstimationReport_Expected)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*id.*", ".*creationDate.*", ".*report.*")
                .isEqualTo(initialCostSavingsReport.getBody()));

        // Checks on Workload Inventory Report
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(workloadInventoryReport.getBody().getContent().size()).isEqualTo(14); // OK
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().flatMap(e -> e.getWorkloads().stream()).distinct().count()).isEqualTo(7); // OK
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().filter(e -> e.getWorkloads().contains("Red Hat JBoss EAP")).count()).isEqualTo(2);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().map(WorkloadInventoryReportModel::getOsName).distinct().count()).isEqualTo(4);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().filter(e -> e.getOsName().contains("CentOS 7 (64-bit)")).count()).isEqualTo(2);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().map(WorkloadInventoryReportModel::getComplexity).distinct().count()).isEqualTo(3);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().filter(e -> e.getComplexity().contains("Unknown")).count()).isEqualTo(1);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().flatMap(e -> e.getRecommendedTargetsIMS().stream()).distinct().count()).isEqualTo(3);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().filter(e -> e.getRecommendedTargetsIMS().contains("OSP")).count()).isEqualTo(11);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().flatMap(e -> e.getFlagsIMS().stream()).distinct().count()).isEqualTo(2);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().filter(e -> e.getFlagsIMS().contains("Shared Disk")).count()).isEqualTo(2);
            softly.assertThat(workloadInventoryReport.getBody().getContent().stream().filter(e -> e.getOsName().contains("ServerNT") && e.getWorkloads().contains("Microsoft SQL Server")).count()).isEqualTo(1);
        });


        // Checks on Workload Summary Report
        WorkloadSummaryReportModel workloadSummaryReport_Expected = new ObjectMapper().readValue(IOUtils.resourceToString("cfme_inventory-20190912-demolab-withssa-workload-summary-report.json", StandardCharsets.UTF_8, EndToEndTest.class.getClassLoader()), WorkloadSummaryReportModel.class);

        assertThat(workloadSummaryReport_Expected)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*id.*", ".*creationDate.*",  ".*report.*", ".*workloadsDetectedOSTypeModels.*", ".*scanRunModels.*")
                .isEqualTo(workloadSummaryReport.getBody());

        // ScanRunModels
        SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(workloadSummaryReport_Expected.getScanRunModels().stream().allMatch(e -> workloadSummaryReport.getBody().getScanRunModels().stream()
                            .filter(o -> o.getTarget().equals(e.getTarget()) && o.getType().equals(e.getType()) && o.getDate().equals(e.getDate())).count() > 0))
                            .isTrue();
                    softly.assertThat(workloadSummaryReport.getBody().getScanRunModels().stream().allMatch(e -> workloadSummaryReport_Expected.getScanRunModels().stream()
                            .filter(o -> o.getTarget().equals(e.getTarget()) && o.getType().equals(e.getType()) && o.getDate().equals(e.getDate())).count() > 0))
                            .isTrue();
                    softly.assertThat(workloadSummaryReport.getBody().getScanRunModels().size()).isEqualTo(workloadSummaryReport_Expected.getScanRunModels().size());

                    // WorkloadDetectedOSTypeModels
                    softly.assertThat(workloadSummaryReport_Expected.getWorkloadsDetectedOSTypeModels().stream().allMatch(e -> workloadSummaryReport.getBody().getWorkloadsDetectedOSTypeModels().stream()
                            .filter(o -> o.getOsName().equals(e.getOsName()) && o.getTotal().equals(e.getTotal())).count() > 0))
                            .isTrue();
                    softly.assertThat(workloadSummaryReport.getBody().getWorkloadsDetectedOSTypeModels().stream().allMatch(e -> workloadSummaryReport_Expected.getWorkloadsDetectedOSTypeModels().stream()
                            .filter(o -> o.getOsName().equals(e.getOsName()) && o.getTotal().equals(e.getTotal())).count() > 0))
                            .isTrue();
                    softly.assertThat(workloadSummaryReport.getBody().getWorkloadsDetectedOSTypeModels().size()).isEqualTo(workloadSummaryReport_Expected.getWorkloadsDetectedOSTypeModels().size());
                });

        // Performance test
        new RestTemplate().postForEntity("http://localhost:" + serverPort + "/api/xavier/upload", getRequestEntityForUploadRESTCall("cfme_inventory-20190829-16128-uq17dx.tar.gz"), String.class);
        await()
            .atMost(timeout, TimeUnit.MILLISECONDS)
            .with().pollInterval(Duration.ONE_HUNDRED_MILLISECONDS)
            .until(() -> {
                ResponseEntity<WorkloadSummaryReportModel> workloadSummaryReport_PerformanceTest = new RestTemplate().exchange("http://localhost:" + serverPort + "/api/xavier/report/2/workload-summary", HttpMethod.GET, getRequestEntity(), new ParameterizedTypeReference<WorkloadSummaryReportModel>() {});
                return (workloadSummaryReport_PerformanceTest != null &&
                        workloadSummaryReport_PerformanceTest.getStatusCodeValue() == 200 &&
                        workloadSummaryReport_PerformanceTest.getBody() != null &&
                        workloadSummaryReport_PerformanceTest.getBody().getSummaryModels() != null);
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
        LinkedMultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=filex; filename=" + filename);
        fileMap.add("Content-type", "application/zip");
        body.add("file", new HttpEntity<>(IOUtils.resourceToByteArray(filename, EndToEndTest.class.getClassLoader()), fileMap));

        // params Body parts
        body.add("percentageOfHypervisorsMigratedOnYear1", "50");
        body.add("percentageOfHypervisorsMigratedOnYear2", "25");
        body.add("percentageOfHypervisorsMigratedOnYear3", "25");
        body.add("yearOverYearGrowthRatePercentage", "5");
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
        headers.set("x-rh-identity", "eyJlbnRpdGxlbWVudHMiOnsiaW5zaWdodHMiOnsiaXNfZW50aXRsZWQiOnRydWV9LCJvcGVuc2hpZnQiOnsiaXNfZW50aXRsZWQiOnRydWV9LCJzbWFydF9tYW5hZ2VtZW50Ijp7ImlzX2VudGl0bGVkIjpmYWxzZX0sImh5YnJpZF9jbG91ZCI6eyJpc19lbnRpdGxlZCI6dHJ1ZX19LCJpZGVudGl0eSI6eyJpbnRlcm5hbCI6eyJhdXRoX3RpbWUiOjAsImF1dGhfdHlwZSI6Imp3dC1hdXRoIiwib3JnX2lkIjoiNjM0MDA1NiIsICJmaWxlbmFtZSI6ImNsb3VkZm9ybXMtZXhwb3J0LXYxXzBfMC1tdWx0aXBsZS1maWxlcy50YXIuZ3oiLCJvcmlnaW4iOiJ4YXZpZXIiLCJjdXN0b21lcmlkIjoiQ0lEODg4IiwgImFuYWx5c2lzSWQiOiIxIn0sImFjY291bnRfbnVtYmVyIjoiMTQ2MDI5MCIsICJ1c2VyIjp7ImZpcnN0X25hbWUiOiJNYXJjbyIsImlzX2FjdGl2ZSI6dHJ1ZSwiaXNfaW50ZXJuYWwiOnRydWUsImxhc3RfbmFtZSI6IlJpenppIiwibG9jYWxlIjoiZW5fVVMiLCJpc19vcmdfYWRtaW4iOmZhbHNlLCJ1c2VybmFtZSI6Im1yaXp6aUByZWRoYXQuY29tIiwiZW1haWwiOiJtcml6emkrcWFAcmVkaGF0LmNvbSJ9LCJ0eXBlIjoiVXNlciJ9fQ==");
        headers.set("username", "mrizzi@redhat.com");
        return headers;
    }
}

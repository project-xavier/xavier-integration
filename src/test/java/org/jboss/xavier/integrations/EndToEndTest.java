package org.jboss.xavier.integrations;

import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.util.Streams;
import org.jboss.xavier.Application;
import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
import org.jboss.xavier.integrations.jpa.service.InitialSavingsEstimationReportService;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.BinaryBody;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;


@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = EndToEndTest.Initializer.class)
@ActiveProfiles("test")
public class EndToEndTest {

    @ClassRule
    public static GenericContainer activemq = new GenericContainer<>("vromero/activemq-artemis")
            .withExposedPorts(61616)
            .withEnv("DISABLE_SECURITY", "true");

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer()
            .withEnv("KAFKA_CREATE_TOPICS", "platform.upload.xavier")
            .withExposedPorts(9092);
    
    @Inject
    private InitialSavingsEstimationReportService reportService;
    

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
                    "insights.kafka.host=" + kafka.getBootstrapServers());
        }
    }
    
    @Inject
    CamelContext camelContext;
    
    @Inject
    JmsTemplate jmsTemplate;

    private static ClientAndServer clientAndServer;
    

    
    @Before
    public void setupMockServer() {
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
                                BinaryBody body = new BinaryBody(IOUtils.resourceToByteArray("cloudforms-export-v1.tar.gz", EndToEndTest.class.getClassLoader()));
                                return response()
                                        .withHeader("Content-Type", "application/zip")
                                        .withHeader("Accept-Ranges", "bytes")
                                        .withHeader("Content-Length", Integer.toString(body.getRawBytes().length))
                                        .withBody(body);
                            } catch (IOException e) {
                                return notFoundResponse();
                            }
                        });        
        
        clientAndServer.when(request()
                                .withPath("/services/kie-server"))
                        .respond(myrequest -> {
                            try {
                                return response()
                                        .withHeader("Content-Type", "text/xml")
                                        .withStatusCode(200)
                                        .withBody(IOUtils.resourceToString("kie-server-response.xml", StandardCharsets.UTF_8, EndToEndTest.class.getClassLoader()));
                            } catch (IOException e) {
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

        // when
        // Start the camel route as if the UI was sending the file to the Camel Rest Upload route
        new RestTemplate().postForEntity("http://localhost:8080/api/xavier/upload", getRequestEntityForRESTCall(), String.class);

        // then
        Thread.sleep(5000); //TODO check another approach

        // Check database
        Page<InitialSavingsEstimationReportModel> reports = reportService.findReports();
        assertThat(Streams.stream(reports.iterator()).count()).isGreaterThan(0);
        assertThat(Streams.stream(reports.iterator()).anyMatch(e -> e.getEnvironmentModel().getHypervisors() == 2)).isTrue();
        assertThat(Streams.stream(reports.iterator()).anyMatch(e -> e.getSourceCostsModel().getYear1Server() == 42)).isTrue();

        // Check the rest endpoint to retrieve the report
        Map<String, String> params = new HashMap<>();
        params.put("summary", "false");
        String responseReport = new RestTemplate().getForObject("http://localhost:8080/api/xavier/report?summary={summary}", String.class, params);
        assertThat(responseReport).contains("\"year1RhvGrandTotalValue\":187000.0");
        assertThat(responseReport).contains("\"hypervisors\":2,\"year1Hypervisor\":20");
        
        camelContext.stop();
    }

    @NotNull
    private HttpEntity<MultiValueMap<String, Object>> getRequestEntityForRESTCall() throws IOException {

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("x-rh-insights-request-id", "2544925e825b4f3f9418c88556541776");
        headers.set("x-rh-identity", "eyJlbnRpdGxlbWVudHMiOnsiaW5zaWdodHMiOnsiaXNfZW50aXRsZWQiOnRydWV9LCJvcGVuc2hpZnQiOnsiaXNfZW50aXRsZWQiOnRydWV9LCJzbWFydF9tYW5hZ2VtZW50Ijp7ImlzX2VudGl0bGVkIjpmYWxzZX0sImh5YnJpZF9jbG91ZCI6eyJpc19lbnRpdGxlZCI6dHJ1ZX19LCJpZGVudGl0eSI6eyJpbnRlcm5hbCI6eyJhdXRoX3RpbWUiOjAsImF1dGhfdHlwZSI6Imp3dC1hdXRoIiwib3JnX2lkIjoiNjM0MDA1NiIsICJmaWxlbmFtZSI6ImNsb3VkZm9ybXMtZXhwb3J0LXYxLnRhci5neiIsIm9yaWdpbiI6Im1hLXhhdmllciIsImN1c3RvbWVyaWQiOiJDSUQ4ODgifSwiYWNjb3VudF9udW1iZXIiOiIxNDYwMjkwIiwgInVzZXIiOnsiZmlyc3RfbmFtZSI6Ik1hcmNvIiwiaXNfYWN0aXZlIjp0cnVlLCJpc19pbnRlcm5hbCI6dHJ1ZSwibGFzdF9uYW1lIjoiUml6emkiLCJsb2NhbGUiOiJlbl9VUyIsImlzX29yZ19hZG1pbiI6ZmFsc2UsInVzZXJuYW1lIjoibXJpenppQHJlZGhhdC5jb20iLCJlbWFpbCI6Im1yaXp6aStxYUByZWRoYXQuY29tIn0sInR5cGUiOiJVc2VyIn19");

        // Body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        // File Body part
        String filename = "cloudforms-export-v1-multiple-files.tar.gz";
        LinkedMultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=filex; filename=" +filename);
        fileMap.add("Content-type", "application/zip");
        body.add("file", new HttpEntity<>(IOUtils.resourceToByteArray(filename, EndToEndTest.class.getClassLoader()), fileMap));
        
        // params Body parts
        body.add("dummy", "CID12345");
        body.add("year1hypervisorpercentage", "10");
        body.add("year2hypervisorpercentage", "10");
        body.add("year3hypervisorpercentage", "10");
        body.add("growthratepercentage", "10");
        body.add("sourceproductindicator", "10");


        return new HttpEntity<>(body, headers);
    }
}

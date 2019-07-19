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
import org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel;
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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;


@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class})
@ContextConfiguration(initializers = EndToEndIT.Initializer.class)
@ActiveProfiles("test")
//@Ignore
public class EndToEndIT {

    @ClassRule
    public static GenericContainer activemq = new GenericContainer<>("rmohr/activemq").withExposedPorts(61616);

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer()
            .withEnv("KAFKA_CREATE_TOPICS", "platform.upload.xavier")
            .withExposedPorts(9092);
    
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
           
            EnvironmentTestUtils.addEnvironment("environment", configurableApplicationContext.getEnvironment(),
                    "spring.activemq.broker-url=" + activemq.getContainerIpAddress() + ":" + activemq.getMappedPort(61616),
                    "amq.server=" + activemq.getContainerIpAddress(),
                    "amq.port=" + activemq.getMappedPort(61616),
                    "insights.upload.host=localhost:8000",
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
                    return response().withStatusCode(202).withBody("success").withHeader("Content-Type", "application/zip");
                });
        
        clientAndServer.when(request()
                                .withPath("/insights-upload-perm-test/440c88f9-5930-416e-9799-fa01d156df29"))
                        .respond(myrequest -> {
                            try {
                                return response()
                                        .withHeader("Content-Type", "application/zip")
                                        .withBody(new BinaryBody(IOUtils.resourceToByteArray("platform.upload.xavier.json.gz", EndToEndIT.class.getClassLoader())));
                            } catch (IOException e) {
                                return notFoundResponse();
                            }
                        });        
        
        clientAndServer.when(request()
                                .withPath("/insights-upload-perm-test2"))
                        .respond(myrequest -> {
                            try {
                                return response()
                                        .withHeader("Content-Type", "application/zip")
                                        .withBody(new BinaryBody(IOUtils.resourceToByteArray("platform.upload.xavier.json.gz", EndToEndIT.class.getClassLoader())));
                            } catch (IOException e) {
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
        
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("cloudforms-export-v1-multiple-files.tar.gz");
        assertThat(resourceAsStream).isNotNull();

        String mimeHeader = "----------------------------378483299686133026113807\n" +
                "Content-Disposition: form-data; name=\"redhat\"; filename=\"cloudforms-export-v1-multiple-files.tar.gz\"\n" +
                "Content-Type: application/zip\n\n";
        String mimeFooter = getMultipartParam("customerid", "CID12345") +
                            getMultipartParam("year1hypervisorpercentage", "10") +
                            getMultipartParam("year2hypervisorpercentage", "10") +
                            getMultipartParam("year3hypervisorpercentage", "10") +
                            getMultipartParam("growthratepercentage", "10") +
                            getMultipartParam("sourceproductindicator", "10") +
                "\n----------------------------378483299686133026113807\n" +
                "Content-Disposition: form-data; name=\"year1hypervisorpercentage\"\n" +
                "\n" +
                "CID12345 \n" +
                "\n----------------------------378483299686133026113807--\n";
      
        SequenceInputStream sequenceInputStream = new SequenceInputStream(new SequenceInputStream(new ByteArrayInputStream(mimeHeader.getBytes()), resourceAsStream), new ByteArrayInputStream(mimeFooter.getBytes()));

        /*
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<byte[]> outputStream = new RestTemplate(messageConverters).exchange("http://localhost:8000/insights-upload-perm-test2", HttpMethod.GET, entity, byte[].class, "1");
        GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(outputStream.getBody()));
        System.out.println(" Descarga : " + IOUtils.toString(gzipInputStream, StandardCharsets.UTF_8));
        */
        
        String output = new RestTemplate().postForObject("http://localhost:8000/api/ingress/v1/upload", IOUtils.toString(sequenceInputStream, "UTF-8"), String.class);

        camelContext.setTracing(true);
        camelContext.start();
        
        // Here we receive the message.
        UploadFormInputDataModel message = (UploadFormInputDataModel) jmsTemplate.receiveAndConvert("inputDataModel");

        assertThat(message).isNotNull();
        assertThat(message.getHypervisor()).isEqualTo(1);
        assertThat(message.getTotalDiskSpace()).isEqualTo(281951062016L);
        
    }

    @NotNull
    private String getMultipartParam(final String param, final String value) {
        return "\n----------------------------378483299686133026113807\n" +
                "Content-Disposition: form-data; name=\"" + param + "\"\n" +
                "\n" +
                "\"" + value + "\" \n";
    }
}


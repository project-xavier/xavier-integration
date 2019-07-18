package org.jboss.xavier.integrations;

import io.specto.hoverfly.junit.rule.HoverflyRule;
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
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static io.specto.hoverfly.junit.core.HoverflyConfig.localConfigs;
import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers.startsWith;
import static org.assertj.core.api.Assertions.assertThat;

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
//            .withEnv("advertised.host.name","localhost")
            .withExposedPorts(9092);
    
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
           
            EnvironmentTestUtils.addEnvironment("environment", configurableApplicationContext.getEnvironment(),
                    "spring.activemq.broker-url=" + activemq.getContainerIpAddress() + ":" + activemq.getMappedPort(61616),
                    "amq.server=" + activemq.getContainerIpAddress(),
                    "amq.port=" + activemq.getMappedPort(61616),
                    "insights.upload.host=www.myservice.com",
                    "insights.kafka.host=" + kafka.getBootstrapServers());
        }
    }
    
    @Inject
    CamelContext camelContext;
    
    @Inject
    JmsTemplate jmsTemplate;

    @ClassRule
    public static HoverflyRule hoverflyRule;

    static {
        try {
            hoverflyRule = HoverflyRule.inSimulationMode(dsl(
                    service("www.myservice.com")
                            .post("/api/ingress/v1/upload")
                                .anyBody()
                                .anyQueryParams()
                                .willReturn(success("hola", "text/json"))
                            .anyMethod(startsWith("/insights-upload-perm-test"))
                                .anyBody()
                                .anyQueryParams()
                                .willReturn(success(IOUtils.toString(EndToEndIT.class.getClassLoader().getResourceAsStream("cloudforms-export-v1.tar.gz"), StandardCharsets.UTF_8), "application/zip")
            )));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendKafkaMessageToSimulateInsightsUploadProcess() throws ExecutionException, InterruptedException, IOException {
        String body = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("platform.upload.xavier.json"), Charset.forName("UTF-8"));
        body = body.replaceAll("http://172.17.0.1:9000", "http://www.myservice.com");

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
        
        localConfigs().logToStdOut();
        
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
        
        String output = new RestTemplate().postForObject("http://www.myservice.com/api/ingress/v1/upload", IOUtils.toString(sequenceInputStream, "UTF-8"), String.class);
        System.out.println("Output from hoverfly : " + output);

        camelContext.setTracing(true);
        camelContext.start();
        
        sendKafkaMessageToSimulateInsightsUploadProcess();

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


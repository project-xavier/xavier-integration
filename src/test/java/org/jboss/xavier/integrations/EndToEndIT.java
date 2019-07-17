package org.jboss.xavier.integrations;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
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

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class})
@ContextConfiguration(initializers = EndToEndIT.Initializer.class)
@ActiveProfiles("test")
@Ignore
public class EndToEndIT {

    @ClassRule
    public static GenericContainer activemq = new GenericContainer<>("rmohr/activemq").withExposedPorts(61616);

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer()
            .withEnv("KAFKA_CREATE_TOPICS", "platform.upload.xavier")
            .withEnv("advertised.host.name","localhost");
    
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
           
            EnvironmentTestUtils.addEnvironment("environment", configurableApplicationContext.getEnvironment(),
                    "spring.activemq.broker-url=tcp://" + activemq.getContainerIpAddress() + ":" + activemq.getMappedPort(61616),
                    "amq.server=tcp://" + activemq.getContainerIpAddress(),
                    "amq.port=" + activemq.getMappedPort(61616),
                    "insights.kafka.host=" + kafka.getBootstrapServers());
        }
    }
    
    @Inject
    CamelContext camelContext;
    
    @Inject
    JmsTemplate jmsTemplate;
    
    @Value("${insights.kafka.upload.topic}")
    String kakfaTopic;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8080));

  
    public class ExampleTransformer extends ResponseDefinitionTransformer {
        @Override
        public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, FileSource files, Parameters parameters) {
            try {
                EndToEndIT.this.sendKafkaMessageToSimulateInsightsUploadProcess();
                return new ResponseDefinitionBuilder().build();
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseDefinitionBuilder()
                        .withBody("Error : " + e.getMessage())
                        .withStatus(400)
                        .build();
            }
        }
        @Override
        public String getName() {
            return "dynamic-transformer";
        }
    
    }
    
    @Before
    public void setup() throws IOException {
        new WireMockServer(wireMockConfig().extensions(new ExampleTransformer()));
        
        // insights-upload simulation
        wireMockRule.stubFor(any(urlPathEqualTo("/api/ingress/v1/upload"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/json")
                        .withStatus(200)
                        .withBody("{}")
                        .withTransformers("dynamic-transformer")));        
        wireMockRule.stubFor(any(urlPathEqualTo("/insights-upload-perm-test"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/json")
                        .withBody(IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("cloudforms-export-v1.tar.gz"), Charset.forName("UTF-8")))));
    }

    @NotNull
    private void sendKafkaMessageToSimulateInsightsUploadProcess() throws ExecutionException, InterruptedException, IOException {
        String body = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("platform.upload.xavier.json"), Charset.forName("UTF-8"));
        body.replaceAll("172.17.0.1:9000", "localhost:8080");
        
        final ProducerRecord<Long, String> record = new ProducerRecord<>(kakfaTopic, body );

        RecordMetadata metadata = createKafkaProducer().send(record).get();
    }

    private static Producer<Long, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers() + ":" + kafka.getMappedPort(29092));
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    @Test
    public void end2endTest() throws Exception {
        camelContext.start();
        
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
        
        String output = new RestTemplate().postForObject("http://localhost:8080/api/ingress/v1/upload", IOUtils.toString(sequenceInputStream, "UTF-8"), String.class);
        
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


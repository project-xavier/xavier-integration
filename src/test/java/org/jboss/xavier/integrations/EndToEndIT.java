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
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
public class EndToEndIT {
    @Inject
    CamelContext camelContext;
    
    @Value("${insights.kafka.upload.topic}")
    String kakfaTopic;
    
    @Rule
    public KafkaContainer kafka = new KafkaContainer().withNetworkAliases("kafka");
    
    @Rule
    public GenericContainer activemq = new GenericContainer<>("rmhor:activemq").withExposedPorts(61616).withNetworkAliases("activemq");    
    
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(80));
    
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
        body.replaceAll("172.17.0.1:9000", "localhost:80");
        
        final ProducerRecord<Long, String> record = new ProducerRecord<>(kakfaTopic, body );

        RecordMetadata metadata = createKafkaProducer().send(record).get();
    }

    private static Producer<Long, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:29092");
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
        
        String output = new RestTemplate().postForObject("localhost:8080/api/xavier/upload", sequenceInputStream, String.class);
        
        //TODO Check JMS receives the message
        
    }

    @NotNull
    private String getMultipartParam(final String param, final String value) {
        return "\n----------------------------378483299686133026113807\n" +
                "Content-Disposition: form-data; name=\"" + param + "\"\n" +
                "\n" +
                "\"" + value + "\" \n";
    }
}


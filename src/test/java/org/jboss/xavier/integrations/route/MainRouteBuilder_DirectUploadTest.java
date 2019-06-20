package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.camel.test.spring.UseAdviceWith;
import org.apache.commons.io.IOUtils;
import org.jboss.xavier.integrations.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("direct:store")
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class}) 
@ActiveProfiles("test")
public class MainRouteBuilder_DirectUploadTest {
    @Autowired
    CamelContext camelContext;

    @EndpointInject(uri = "mock:direct:store")
    private MockEndpoint mockStore;

    @Test
    public void mainRouteBuilder_routeDirectUpload_ContentWithSeveralFilesGiven_ShouldReturnSameNumberOfMessages() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);
        mockStore.expectedMessageCount(5);

        //When
        camelContext.start();
        camelContext.startRoute("direct-upload");
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("mime-message-several-files-sample.txt");

        camelContext.createProducerTemplate().sendBodyAndHeader("direct:upload", IOUtils.toString(resourceAsStream, Charset.forName("UTF-8")), "Content-Type", "multipart/mixed");

        //Then
        mockStore.assertIsSatisfied();
        assertThat(mockStore.getExchanges().stream().filter(e -> "pepito".equalsIgnoreCase(e.getIn().getHeader("nombre", String.class))).collect(Collectors.toList()).size()).isEqualTo(5);

        camelContext.stop();
    }
    
    @Test
    public void mainRouteBuilder_routeDirectUpload_ContentWithZipFilesGiven_ShouldReturn1MessagePerFileInsideZip() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);
        mockStore.expectedMessageCount(3);

        //When
        camelContext.start();
        camelContext.startRoute("direct-upload");


        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("txt-files-samples.zip");
        assertThat(resourceAsStream).isNotNull();
        
        String mimeHeader = "----------------------------378483299686133026113807\n" +
                "Content-Disposition: form-data; name=\"redhat\"; filename=\"txt-files-samples.zip\"\n" +
                "Content-Type: application/zip\n\n";
        String mimeFooter = "\n----------------------------378483299686133026113807--\n";
        SequenceInputStream sequenceInputStream = new SequenceInputStream(new SequenceInputStream(new ByteArrayInputStream(mimeHeader.getBytes()), resourceAsStream), new ByteArrayInputStream(mimeFooter.getBytes()));

        camelContext.createProducerTemplate().sendBodyAndHeader("direct:upload", sequenceInputStream, "Content-Type", "multipart/mixed");

        //Then
        mockStore.assertIsSatisfied();

        camelContext.stop();
    }
    

    
}
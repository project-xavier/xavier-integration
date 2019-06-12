package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.camel.test.spring.UseAdviceWith;
import org.apache.commons.io.IOUtils;
import org.jboss.xavier.integrations.Application;
import org.jboss.xavier.integrations.route.model.RHIdentity;
import org.jboss.xavier.integrations.route.model.notification.FilePersistedNotification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.model.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.apache.camel.builder.Builder.constant;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("http4://oldhost|direct:calculate")
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class}) 
@ActiveProfiles("test")
public class MainRouteBuilder_DirectDownloadTest {
    @Autowired
    CamelContext camelContext;
    
    @Autowired
    MainRouteBuilder mainRouteBuilder;

    @EndpointInject(uri = "mock:http4://oldhost")
    private MockEndpoint mockOldHost;    
    
    @EndpointInject(uri = "mock:direct:calculate")
    private MockEndpoint mockCalculate;

    @Before
    public void setup() throws Exception {
        camelContext.getRouteDefinition("download-file").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() {
                //weaveByToUri("http4://oldhost").after().process(exchange -> exchange.getIn().setBody("--file-from-insights--"));
            }
        });
    }
    
    @Test
    public void mainRouteBuilder_DirectDownloadFile_PersistedNotificationGiven_ShouldCallFileWithGivenHeaders() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);
        mockCalculate.expectedMessageCount(1);
        
        mockOldHost.expectedHeaderReceived("customerid", "CIDXXXX1234");
        //mockOldHost.expectedHeaderReceived("ExchangXXe.HTTP_URI", "http://dummxyurl.com");

        //When
        camelContext.start();
        camelContext.startRoute("download-file");
        FilePersistedNotification body = FilePersistedNotification.builder().url("http://dummyurl.com").category("cat").service("xavier").b64_identity(mainRouteBuilder.getRHIdentity("CID1234", "ficherito.txt")).build();

        camelContext.createProducerTemplate().sendBody("direct:download-file", body);

        //Then
        mockOldHost.assertIsSatisfied();
        mockCalculate.assertIsSatisfied();

        camelContext.stop();
    }
    
}
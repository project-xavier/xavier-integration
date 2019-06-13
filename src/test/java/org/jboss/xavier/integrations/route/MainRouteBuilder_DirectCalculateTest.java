package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.camel.test.spring.UseAdviceWith;
import org.apache.commons.io.IOUtils;
import org.jboss.xavier.integrations.Application;
import org.jboss.xavier.integrations.migrationanalytics.input.InputDataModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("jms:queue:inputDataModel")
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class}) 
@ActiveProfiles("test")
public class MainRouteBuilder_DirectCalculateTest {
    @Autowired
    CamelContext camelContext;
    
    @Autowired
    MainRouteBuilder mainRouteBuilder;

    @EndpointInject(uri = "mock:jms:queue:inputDataModel")
    private MockEndpoint mockJmsQueue;    
    
    @Test
    public void mainRouteBuilder_DirectDownloadFile_PersistedNotificationGiven_ShouldCallFileWithGivenHeaders() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);

        String customerId = "CID123";
        String fileName = "cloudforms-export.json";
        mockJmsQueue.expectedBodiesReceived(InputDataModel.builder().customerId(customerId).fileName(fileName).numberOfHosts(2).totalDiskSpace(1826703278080L).build());
        
        //When
        camelContext.start();
        camelContext.startRoute("calculate");
        String body = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(fileName), Charset.forName("UTF-8"));

        Map<String, Object> headers = new HashMap<>();
        headers.put("customerid", customerId);
        headers.put("filename", fileName);
        camelContext.createProducerTemplate().sendBodyAndHeaders("direct:calculate", body, headers);

        //Then
        mockJmsQueue.assertIsSatisfied();

        camelContext.stop();
    }
    
}
package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.camel.test.spring.UseAdviceWith;
import org.jboss.xavier.integrations.Application;
import org.jboss.xavier.integrations.DecisionServerHelper;
import org.jboss.xavier.integrations.migrationanalytics.input.InputDataModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.command.BatchExecutionCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("http:*")
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class}) 
@ActiveProfiles("test")
public class XmlRoutes_DirectDecisionServerTest {
    @Autowired
    CamelContext camelContext;

    @EndpointInject(uri="mock:http:{{kieserver.devel-service}}/{{kieserver.path}}")
    MockEndpoint kieServer;
    
    @Autowired
    DecisionServerHelper decisionServerHelper;

    @Test
    public void mainRouteBuilder_DirectDecisionServer_ContentWithSeveralFilesGiven_ShouldReturnSameNumberOfMessages() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);
        String expectedBody = "<?xml version='1.0' encoding='UTF-8'?><batch-execution lookup=\"kiesession0\"><insert><com.myspace.sample__analytics.pojo.input.InputDataModel><id>1</id><customerId>CID12234</customerId><fileName>ficherito.json</fileName><numberOfHosts>10</numberOfHosts><totalDiskSpace>1000</totalDiskSpace></com.myspace.sample__analytics.pojo.input.InputDataModel></insert><fire-all-rules/><query out-identifier=\"output\" name=\"get reports\"/></batch-execution>";
        kieServer.expectedBodiesReceived(expectedBody);

        //When
        camelContext.start();
        camelContext.startRoute("decision-server-rest");
        InputDataModel inputDatamodel = InputDataModel.builder().customerId("CID12234").fileName("ficherito.json").id(1L).totalDiskSpace(1000L).numberOfHosts(10).build();
        BatchExecutionCommand sentBody = decisionServerHelper.createMigrationAnalyticsCommand(inputDatamodel);
        
        camelContext.createProducerTemplate().sendBody("direct:decisionserver", sentBody );
        
        //Then
        kieServer.assertIsSatisfied();
        camelContext.stop();
    }
    
}
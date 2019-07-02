package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.camel.test.spring.UseAdviceWith;
import org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel;
import org.jboss.xavier.integrations.Application;
import org.jboss.xavier.integrations.DecisionServerHelper;
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
        String expectedBody = "<?xml version='1.0' encoding='UTF-8'?><batch-execution lookup=\"kiesession0\"><insert><org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel><id>1</id><customerId>CID12234</customerId><fileName>ficherito.json</fileName><numberOfHosts>10</numberOfHosts><totalDiskSpace>1000</totalDiskSpace></org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel></insert><fire-all-rules/><query out-identifier=\"output\" name=\"get reports\"/></batch-execution>";
        kieServer.expectedBodiesReceived(expectedBody);

        //When
        camelContext.start();
        camelContext.startRoute("decision-server-rest");
        String customerId = "CID123";
        String fileName = "cloudforms-export-v1.json";
        Integer hypervisor = 1;
        Long totaldiskspace = 1000L;
        Integer sourceproductindicator = 1;
        Double year1hypervisorpercentage = 10D;
        Double year2hypervisorpercentage = 20D;
        Double year3hypervisorpercentage = 30D;
        Double growthratepercentage = 7D;
        UploadFormInputDataModel inputDataModel = new UploadFormInputDataModel(customerId, fileName, hypervisor, totaldiskspace, sourceproductindicator, year1hypervisorpercentage, year2hypervisorpercentage, year3hypervisorpercentage, growthratepercentage);
        
        
        BatchExecutionCommand sentBody = decisionServerHelper.createMigrationAnalyticsCommand(inputDataModel);
        
        camelContext.createProducerTemplate().sendBody("direct:decisionserver", sentBody );
        
        //Then
        kieServer.assertIsSatisfied();
        camelContext.stop();
    }
    
}
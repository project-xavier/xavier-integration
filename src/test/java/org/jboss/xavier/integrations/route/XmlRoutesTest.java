package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.camel.test.spring.UseAdviceWith;
import org.jboss.xavier.integrations.Application;
import org.jboss.xavier.integrations.DecisionServerHelper;
import org.jboss.xavier.integrations.migrationanalytics.input.InputDataModel;
import org.jboss.xavier.integrations.migrationanalytics.output.ReportDataModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.model.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("jpa:org.jboss.xavier.integrations.migrationanalytics.output.ReportDataModel|direct:decisionserver")  
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class}) //, properties = {"camel.springboot.java-routes-include-pattern=**/test-route-ma"}) Doesnt work if using UseAdviceWith
@ActiveProfiles("test")
public class XmlRoutesTest 
{
    @Autowired
    CamelContext camelContext;

    @EndpointInject(uri = "mock:jpa:org.jboss.xavier.integrations.migrationanalytics.output.ReportDataModel")
    MockEndpoint mockJPA;
    
    @SpyBean
    DecisionServerHelper decisionServerHelper;
    
    @Before
    public void setup() throws Exception {
        camelContext.getRouteDefinition("test-route-ma").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:inputDataModel");
                weaveById("decisionserver").after().process(exchange -> {
                    System.out.println("ENTRANDO");
                    exchange.getIn().setBody(new ServiceResponse<ExecutionResults>());
                    System.out.println("SALIENDO");
                });
            }
        });     
        doReturn(getReportModelSample()).when(decisionServerHelper).extractReports(any());
    }

    private ReportDataModel getReportModelSample() {
        return ReportDataModel.builder().numberOfHosts(10).totalDiskSpace(100L).totalPrice(1000).build();
    }    
    
    @Test
    public void test0() throws Exception
    {
      
        mockJPA.expectedBodiesReceived(getReportModelSample());

        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);
        camelContext.start();
        camelContext.startRoute("test-route-ma");

        camelContext.createProducerTemplate().sendBody("direct:inputDataModel", getInputDataModelSample());

        mockJPA.assertIsSatisfied();
        
        camelContext.stop();
    }

    private InputDataModel getInputDataModelSample() {
        return InputDataModel.builder().customerId("CID7899").fileName("ficherito.json").numberOfHosts(100).totalDiskSpace(2000L).build();
    }
    
}

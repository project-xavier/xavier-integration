package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.camel.test.spring.UseAdviceWith;
import org.jboss.xavier.integrations.Application;
import org.jboss.xavier.integrations.jpa.service.ReportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;


@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("")
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) 
@ActiveProfiles("test")
public class XmlRoutes_RestReportTest {
    @Autowired
    CamelContext camelContext;

    @Autowired
    private TestRestTemplate restTemplate;
    
    @MockBean
    private ReportService reportService;

    @Test
    public void mainRouteBuilder_RestReport_SummaryParamGiven_ShouldCallFindReportSummary() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);

        //When
        camelContext.start();
        camelContext.startRoute("reports-get-all");
        Map<String, String> variables = new HashMap<>();
        variables.put("summary", "true");
        restTemplate.getForEntity("/camel/report?summary={summary}", String.class, variables);
        
        //Then
        verify(reportService).findReportSummary();
        camelContext.stop();
    }    
    
    @Test
    public void mainRouteBuilder_RestReport_NotSummaryParamGiven_ShouldCallFindReports() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);

        //When
        camelContext.start();
        camelContext.startRoute("reports-get-all");
        Map<String, String> variables = new HashMap<>();
        variables.put("summary", "false");
        restTemplate.getForEntity("/camel/report?summary={summary}", String.class, variables);
        
        //Then
        verify(reportService).findReports();
        camelContext.stop();
    }
    
}
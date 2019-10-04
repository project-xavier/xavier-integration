package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.xstream.XStreamDataFormat;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.camel.test.spring.UseAdviceWith;
import org.apache.commons.io.IOUtils;
import org.jboss.xavier.Application;
import org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel;
import org.jboss.xavier.analytics.pojo.output.AnalysisModel;
import org.jboss.xavier.integrations.DecisionServerHelper;
import org.jboss.xavier.integrations.migrationanalytics.output.ReportDataModel;
import org.jetbrains.annotations.NotNull;
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

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("jpa:org.jboss.xavier.analytics.pojo.output.AnalysisModel|direct:decisionserver")
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class})
@ActiveProfiles("test")
public class XmlRoutes_CallKieExtractReportsTest {
    @Autowired
    CamelContext camelContext;

    @EndpointInject(uri = "mock:jpa:org.jboss.xavier.analytics.pojo.output.AnalysisModel")
    private MockEndpoint mockJPA;

    @SpyBean
    DecisionServerHelper decisionServerHelper;
    
    @Inject
    @Named("xstream-dataformat")
    XStreamDataFormat xStreamDataFormat;

    @Before
    public void setup() throws Exception {
        camelContext.getRouteDefinition("route-ma").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() {
                replaceFromWith("direct:uploadFormInputDataModel");
                weaveById("decisionserver").after().process(exchange -> exchange.getIn().setBody(getKieServiceResponse()));
            }
        });
        doReturn(getReportModelSample()).when(decisionServerHelper).extractReports(any());
    }

    @NotNull
    private ServiceResponse<ExecutionResults> getKieServiceResponse() throws IOException {
        ServiceResponse<ExecutionResults> response = (ServiceResponse<ExecutionResults>) xStreamDataFormat.getXstream().fromXML(IOUtils.resourceToString("kie-server-response-initialcostsavingsreport.xml", StandardCharsets.UTF_8, XmlRoutes_CallKieExtractReportsTest.class.getClassLoader()));
        return response;
    }

    @Test
    public void xmlroutes_directInputDataModel_InputDataModelGiven_ShouldReportDecisionServerHelperValues() throws Exception
    {

        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);
        camelContext.start();
        camelContext.startRoute("route-ma");

        camelContext.createProducerTemplate().sendBody("direct:uploadFormInputDataModel", getInputDataModelSample());

        assertThat(mockJPA.getExchanges().get(0).getIn().getBody(AnalysisModel.class).getInitialSavingsEstimationReportModel().getEnvironmentModel().getHypervisors()).isEqualTo(2);

        camelContext.stop();
    }

    private ReportDataModel getReportModelSample() {
        return ReportDataModel.builder().numberOfHosts(10).totalDiskSpace(100L).totalPrice(1000).build();
    }

    private UploadFormInputDataModel getInputDataModelSample() {
        String customerId = "CID123";
        String fileName = "cloudforms-export-v1.json";
        Integer hypervisor = 1;
        Long totaldiskspace = 1000L;
        Integer sourceproductindicator = 1;
        Double year1hypervisorpercentage = 10D;
        Double year2hypervisorpercentage = 20D;
        Double year3hypervisorpercentage = 30D;
        Double growthratepercentage = 7D;
        Long analysisId = 11L;
        return new UploadFormInputDataModel(customerId, fileName, hypervisor, totaldiskspace, sourceproductindicator, year1hypervisorpercentage, year2hypervisorpercentage, year3hypervisorpercentage, growthratepercentage, analysisId);
    }

}

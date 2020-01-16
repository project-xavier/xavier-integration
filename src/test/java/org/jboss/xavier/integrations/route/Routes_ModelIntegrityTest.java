package org.jboss.xavier.integrations.route;

import org.apache.camel.builder.RouteBuilder;
import org.jboss.xavier.Application;
import org.jboss.xavier.analytics.pojo.output.AnalysisModel;
import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
import org.jboss.xavier.integrations.jpa.repository.AnalysisRepository;
import org.jboss.xavier.integrations.jpa.service.AnalysisService;
import org.jboss.xavier.integrations.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Routes_ModelIntegrityTest extends XavierCamelTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private AnalysisRepository analysisRepository;

    @Value("${camel.component.servlet.mapping.context-path}")
    String camel_context;

    @Before
    public void setup() {
        camel_context = camel_context.substring(0, camel_context.indexOf("*"));
    }

    /**
     * https://issues.redhat.com/browse/MIGENG-371
     * Fixing More than one row with the given identifier was found X,
     * for class: org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel
     */
    @Test
    public void Routes_ModelIntegrity_NoOrphanInitialSavingsEstimationReportModelRemainsAfterAnalysisModelUpdate() throws Exception {
        //Given
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("rest:post:/create-analysis-test")
                        .routeId("create-analysis-test")
                        .to("direct:check-authenticated-request")
                        .process(exchange -> {
                            AnalysisModel analysisModel = analysisService.buildAndSave(
                                    "reportName",
                                    "reportDescription",
                                    "payloadName",
                                    "mrizzi@redhat.com"
                            );

                            InitialSavingsEstimationReportModel initialSavingsEstimationReportModel = new InitialSavingsEstimationReportModel();
                            analysisService.setInitialSavingsEstimationReportModel(initialSavingsEstimationReportModel, analysisModel.getId());
                        })
                        .to("direct:replace-analysis-initialSavingsEstimation");

                from("direct:replace-analysis-initialSavingsEstimation")
                        .routeId("replace-analysis-initialSavingsEstimation")
                        .process(exchange -> {
                            List<AnalysisModel> analysisModels = analysisRepository.findAll();
                            AnalysisModel analysisModel = analysisModels.get(0);

                            // Force expected failure replacing initialSavingsEstimationReportModel by a new Object
                            InitialSavingsEstimationReportModel initialSavingsEstimationReportModel = new InitialSavingsEstimationReportModel();
                            analysisService.setInitialSavingsEstimationReportModel(initialSavingsEstimationReportModel, analysisModel.getId());

                            // If code below executes with error we will see `More than one row with the given identifier was found: 1`
                            analysisRepository.findAll();
                        });
            }
        });

        //When
        camelContext.start();
        TestUtil.startUsernameRoutes(camelContext);
        camelContext.startRoute("create-analysis-test");
        camelContext.startRoute("replace-analysis-initialSavingsEstimation");
        camelContext.startRoute("reports-get-all");

        HttpHeaders headers = new HttpHeaders();
        headers.set(TestUtil.HEADER_RH_IDENTITY, TestUtil.getBase64RHIdentity());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response1 = restTemplate.exchange(camel_context + "/create-analysis-test/", HttpMethod.POST, entity, String.class);

        // Then
        assertThat(response1.getStatusCodeValue()).isEqualTo(200);
        camelContext.stop();
    }

}

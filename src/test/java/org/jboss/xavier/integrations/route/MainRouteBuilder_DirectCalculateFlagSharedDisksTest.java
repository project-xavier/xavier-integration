package org.jboss.xavier.integrations.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.commons.io.IOUtils;
import org.jboss.xavier.analytics.pojo.output.AnalysisModel;
import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.integrations.jpa.service.AnalysisService;
import org.jboss.xavier.integrations.jpa.service.WorkloadInventoryReportService;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainRouteBuilder_DirectCalculateFlagSharedDisksTest extends XavierCamelTest {
    @Inject
    AnalysisService analysisService;

    @MockBean
    private WorkloadInventoryReportService workloadInventoryReportService;

    @Test
    public void mainRouteBuilder_DirectCalculate_JSONGiven_ShouldReturnExpectedCalculatedValues() throws Exception {
        //Given
        camelContext.getRouteDefinition("flags-shared-disks").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpointsAndSkip("direct:reevaluate-workload-inventory-reports");
            }
        });

        AnalysisModel analysisModel = analysisService.buildAndSave("report name", "report desc", "file name", "user name");
        Set<String> expectedVmNamesWithSharedDisk = new HashSet<>();
        expectedVmNamesWithSharedDisk.add("dev-windows-server-2008-TEST");
        expectedVmNamesWithSharedDisk.add("james-db-03-copy");
        expectedVmNamesWithSharedDisk.add("dev-windows-server-2008");
        expectedVmNamesWithSharedDisk.add("pemcg-rdm-test");
        List<WorkloadInventoryReportModel> workloadInventoryReportModels = new ArrayList<>(expectedVmNamesWithSharedDisk.size());
        expectedVmNamesWithSharedDisk.forEach(vm -> {
            WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
            workloadInventoryReportModel.setVmName(vm);
            workloadInventoryReportModels.add(workloadInventoryReportModel);
        });
        when(workloadInventoryReportService.findByAnalysisOwnerAndAnalysisId("user name", analysisModel.getId())).thenReturn(workloadInventoryReportModels);

        String customerId = "CID123";
        String fileName = "cloudforms-export-v1.json";
        Long analysisId = analysisModel.getId();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("filename", fileName);
        metadata.put("org_id", customerId);
        metadata.put(RouteBuilderExceptionHandler.ANALYSIS_ID, analysisId.toString());

        Map<String, Object> headers = new HashMap<>();
        headers.put(RouteBuilderExceptionHandler.MA_METADATA, metadata);
        headers.put(RouteBuilderExceptionHandler.USERNAME, "user name");

        //When
        camelContext.start();
        camelContext.startRoute("flags-shared-disks");
        String body = IOUtils.resourceToString(fileName, StandardCharsets.UTF_8, this.getClass().getClassLoader());

        camelContext.createProducerTemplate().sendBodyAndHeaders("direct:flags-shared-disks", body, headers);

        verify(workloadInventoryReportService).saveAll(workloadInventoryReportModels);

        camelContext.stop();
    }

    @Test
    public void mainRouteBuilder_DirectCalculate_JSONOnVersion1_0_0Given_ShouldReturnExpectedCalculatedValues() throws Exception {
        //Given
        camelContext.getRouteDefinition("flags-shared-disks").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpointsAndSkip("direct:reevaluate-workload-inventory-reports");
            }
        });

        AnalysisModel analysisModel = analysisService.buildAndSave("report name", "report desc", "file name", "user name");
        Set<String> expectedVmNamesWithSharedDisk = new HashSet<>();
        expectedVmNamesWithSharedDisk.add("tomcat");
        expectedVmNamesWithSharedDisk.add("lb");
        List<WorkloadInventoryReportModel> workloadInventoryReportModels = new ArrayList<>(expectedVmNamesWithSharedDisk.size());
        expectedVmNamesWithSharedDisk.forEach(vm -> {
            WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
            workloadInventoryReportModel.setVmName(vm);
            workloadInventoryReportModels.add(workloadInventoryReportModel);
        });
        when(workloadInventoryReportService.findByAnalysisOwnerAndAnalysisId("user name", analysisModel.getId())).thenReturn(workloadInventoryReportModels);


        String customerId = "CID123";
        String fileName = "cloudforms-export-v1_0_0.json";
        Long analysisId = analysisModel.getId();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("filename", fileName);
        metadata.put("org_id", customerId);
        metadata.put(RouteBuilderExceptionHandler.ANALYSIS_ID, analysisId.toString());

        Map<String, Object> headers = new HashMap<>();
        headers.put(RouteBuilderExceptionHandler.MA_METADATA, metadata);
        headers.put(RouteBuilderExceptionHandler.USERNAME, "user name");

        //When
        camelContext.start();
        camelContext.startRoute("flags-shared-disks");
        String body = IOUtils.resourceToString(fileName, StandardCharsets.UTF_8, this.getClass().getClassLoader());

        camelContext.createProducerTemplate().sendBodyAndHeaders("direct:flags-shared-disks", body, headers);

        //Then
        verify(workloadInventoryReportService).saveAll(workloadInventoryReportModels);

        camelContext.stop();
    }

    @Test
    public void mainRouteBuilder_DirectReevaluateWorkloadInventoryReport_GivenWorkloadInventoryReports_ShouldUpdateComplexity() throws Exception {
        //Given
        AnalysisModel analysisModel = analysisService.buildAndSave("report name", "report desc", "file name", "user name");


        Long wirId = 1L;
        String wirComplexity = "initialComplexity";

        List<WorkloadInventoryReportModel> workloadInventoryReportModels = new ArrayList<>();

        WorkloadInventoryReportModel workloadInventoryReportModel1 = new WorkloadInventoryReportModel();
        workloadInventoryReportModel1.setId(wirId);
        workloadInventoryReportModel1.setComplexity(wirComplexity);
        workloadInventoryReportModel1.setAnalysis(analysisModel);

        workloadInventoryReportModels.add(workloadInventoryReportModel1);


        when(workloadInventoryReportService.findOneById(wirId)).thenReturn(workloadInventoryReportModel1);


        camelContext.getRouteDefinition("extract-vmworkloadinventory").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpointsAndSkip("direct:decisionserver");
                weaveById("workload-decisionserver")
                        .after()
                        .process(exchange -> exchange.getIn().setBody(IOUtils.resourceToString("kie-server-response-workloadinventoryreport.xml", StandardCharsets.UTF_8, MainRouteBuilder_DirectWorkloadInventoryTest.class.getClassLoader())))
                        .unmarshal().xstream();
            }
        });
        camelContext.getRouteDefinition("reevaluate-workload-inventory-reports").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveById("reevaluate-workload-decisionserver")
                        .after()
                        .process(exchange -> {
                            WorkloadInventoryReportModel workloadInventoryReportModel = exchange.getIn().getBody(WorkloadInventoryReportModel.class);
                            workloadInventoryReportModel.setId(wirId); // Set id since the decisionServer mock is not giving the Id back
                        });
            }
        });

        //When
        camelContext.start();
        camelContext.startRoute("reevaluate-workload-inventory-reports");
        camelContext.startRoute("extract-vmworkloadinventory");

        Exchange result = camelContext.createProducerTemplate().send("direct:reevaluate-workload-inventory-reports", exchange -> {
            exchange.getIn().setBody(workloadInventoryReportModels);
        });

        //Then
        verify(workloadInventoryReportService).saveAll(workloadInventoryReportModels);

        List<WorkloadInventoryReportModel> updatedWorkloadInventoryReportModels = result.getIn().getBody(List.class);
        assertThat(updatedWorkloadInventoryReportModels).isNotNull();
        assertThat(updatedWorkloadInventoryReportModels.size()).isEqualTo(1);
        assertThat(updatedWorkloadInventoryReportModels.get(0).getComplexity()).isEqualTo("complexity"); // "complexity" comes from "kie-server-response-workloadinventoryreport.xml"

        camelContext.stop();
    }

}

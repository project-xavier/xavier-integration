package org.jboss.xavier.integrations.route;

import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.integrations.jpa.service.WorkloadInventoryReportService;
import org.jboss.xavier.integrations.route.strategy.WorkloadInventoryReportModelAggregationStrategy;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Named
public class VMWorkloadInventoryRoutes extends RouteBuilderExceptionHandler {

    @Inject
    WorkloadInventoryReportService workloadInventoryReportService;

    @Value("${parallel.wir}")
    private boolean parallel;

    @Override
    public void configure() throws Exception {
        super.configure();

        from("direct:calculate-vmworkloadinventory").routeId("calculate-vmworkloadinventory")
            .setHeader("KieSessionId", constant("WorkloadInventoryKSession0"))
            .bean("VMWorkloadInventoryCalculator", "calculate(${body}, ${header.${type:org.jboss.xavier.integrations.route.MainRouteBuilder.MA_METADATA}})", false)
            .split(body()).parallelProcessing(parallel).aggregationStrategy(new WorkloadInventoryReportModelAggregationStrategy())
                .setHeader(ANALYSIS_ID, simple("${body." + ANALYSIS_ID + "}", String.class))
                .to("direct:vm-workload-inventory")
            .end()
            .process(exchange -> analysisService.addWorkloadInventoryReportModels(exchange.getIn().getBody(List.class),
                    Long.parseLong(exchange.getIn().getHeader(MA_METADATA, Map.class).get(ANALYSIS_ID).toString())));

        from ("direct:vm-workload-inventory").routeId("extract-vmworkloadinventory")
            .transform().method("decisionServerHelper", "generateCommands(${body}, \"GetWorkloadInventoryReports\", ${header.KieSessionId})")
            .to("direct:decisionserver").id("workload-decisionserver")
            .transform().method("decisionServerHelper", "extractWorkloadInventoryReportModel");

        from("direct:flags-shared-disks").routeId("flags-shared-disks")
            .bean("flagSharedDisksCalculator", "calculate(${body}, ${header.${type:org.jboss.xavier.integrations.route.MainRouteBuilder.MA_METADATA}})", false)
            .process(exchange -> {
                Set<String> vmNamesWithSharedDisk = exchange.getIn().getBody(Set.class);
                List<WorkloadInventoryReportModel> workloadInventoryReportModels = workloadInventoryReportService.findByAnalysisOwnerAndAnalysisId(
                        exchange.getIn().getHeader(USERNAME, String.class),
                        Long.parseLong(exchange.getIn().getHeader(MA_METADATA, Map.class).get(ANALYSIS_ID).toString()));
                List<WorkloadInventoryReportModel> workloadInventoryReportModelsToUpdate = workloadInventoryReportModels.stream()
                    .filter(workloadInventoryReportModel -> vmNamesWithSharedDisk.contains(workloadInventoryReportModel.getVmName()))
                    .peek(workloadInventoryReportModel -> workloadInventoryReportModel.addFlagIMS("Shared Disk")).collect(Collectors.toList());
                exchange.getIn().setBody(workloadInventoryReportModelsToUpdate);
            })
            .to("direct:reevaluate-workload-inventory-reports");

        from("direct:reevaluate-workload-inventory-reports").routeId("reevaluate-workload-inventory-reports")
            .split(body()).parallelProcessing(parallel).aggregationStrategy(new WorkloadInventoryReportModelAggregationStrategy())
            .process(exchange -> {
                WorkloadInventoryReportModel wir = exchange.getIn().getBody(WorkloadInventoryReportModel.class);
                exchange.getIn().setHeader(ANALYSIS_ID, wir.getAnalysis().getId());
            })
                .setHeader("KieSessionId", constant("WorkloadInventoryReevaluateKSession0"))
                .to("direct:vm-workload-inventory").id("reevaluate-workload-decisionserver")
            .end()
            .process(exchange -> {
                String username = exchange.getIn().getHeader(USERNAME, String.class);
                List<WorkloadInventoryReportModel> kieWir = exchange.getIn().getBody(List.class);
                kieWir.forEach(element -> System.out.println("VM: " + element.getVmName() + "Category: " + element.getVmCategory()));
                List<WorkloadInventoryReportModel> updatedWir = kieWir.stream()
                        .map(element -> {
                            WorkloadInventoryReportModel dbWir = workloadInventoryReportService.findOneByOwnerAndId(username, element.getId());
                            dbWir.setComplexity(element.getComplexity());
                            dbWir.setFlagsIMS(element.getFlagsIMS());
                            dbWir.setRecommendedTargetsIMS(element.getRecommendedTargetsIMS());
                            dbWir.setVmCategory(element.getVmCategory());
                            return dbWir;
                        }).collect(Collectors.toList());
                workloadInventoryReportService.saveAll(updatedWir);
                exchange.getIn().setBody(updatedWir);
            });
    }
}

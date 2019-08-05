package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.analytics.pojo.output.workload.summary.SummaryModel;
import org.jboss.xavier.analytics.pojo.output.workload.summary.WorkloadSummaryReportModel;
import org.jboss.xavier.integrations.jpa.service.AnalysisService;
import org.jboss.xavier.integrations.jpa.service.SummaryService;
import org.jboss.xavier.integrations.jpa.service.WorkloadInventoryReportService;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Named
public class WorkloadSummaryReportRoutes extends RouteBuilder {

    private final Logger logger = Logger.getLogger(WorkloadSummaryReportRoutes.class.getName());

    @Inject
    WorkloadInventoryReportService workloadInventoryReportService;

    @Inject
    SummaryService summaryService;

    @Inject
    AnalysisService analysisService;

    @Value("${report.workload.summary.polling.delay}")
    private long delay;

    @Value("${report.workload.summary.polling.max-attempts}")
    private long maxAttempts;

    @Override
    public void configure() {

        from("direct:aggregate-vmworkloadinventory")
            .id("aggregate-vmworkloadinventory")
            .process(exchange -> {
                Integer expectedSize = ((Collection) exchange.getIn().getBody()).size();
                String analysisId = ((Map<String, String>) exchange.getIn().getHeader("MA_metadata")).get(MainRouteBuilder.ANALYSIS_ID);
                List<WorkloadInventoryReportModel> workloadInventoryReportModels  = workloadInventoryReportService.findByAnalysisId(Long.parseLong(analysisId));
                int attempts = 0;
                for (; workloadInventoryReportModels.size() < expectedSize && attempts < maxAttempts; attempts++)
                {
                    Thread.sleep(delay);
                    logger.warning("workloadInventoryReportModels.size() < expectedSize since " + workloadInventoryReportModels.size()  + " < " + expectedSize);
                    workloadInventoryReportModels  = workloadInventoryReportService.findByAnalysisId(Long.parseLong(analysisId));
                }
                if (maxAttempts == attempts) throw new CamelExecutionException("Unable to find the expected " + expectedSize + " WorkloadInventoryReportModels in the DB", exchange);
            })
            .to("direct:calculate-workloadsummaryreportmodel");

        from("direct:calculate-workloadsummaryreportmodel")
            .id("calculate-workloadsummaryreportmodel")
            .process(exchange -> {
                Long analysisId = Long.parseLong(((Map<String, String>) exchange.getIn().getHeader("MA_metadata")).get(MainRouteBuilder.ANALYSIS_ID));
                List<SummaryModel> summaryModels = summaryService.calculateSummaryModels(analysisId);
                // TODO Calculate the other parts of the Workload Summary Report

                // Set the components into the WorkloadSummaryReportModel bean
                WorkloadSummaryReportModel workloadSummaryReportModel = new WorkloadSummaryReportModel();
                workloadSummaryReportModel.setSummaryModels(summaryModels);
                // Set the WorkloadSummaryReportModel into the AnalysisModel
                analysisService.setWorkloadSummaryReportModel(workloadSummaryReportModel, analysisId);
            })
            .to("log:INFO?showBody=true&showHeaders=true");
    }
}

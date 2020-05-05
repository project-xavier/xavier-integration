package org.jboss.xavier.integrations.route;

import org.jboss.xavier.analytics.pojo.output.AnalysisModel;
import org.jboss.xavier.analytics.pojo.output.workload.summary.*;
import org.jboss.xavier.integrations.jpa.service.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Named
public class WorkloadSummaryReportRoutes extends RouteBuilderExceptionHandler {

    @Inject
    WorkloadInventoryReportService workloadInventoryReportService;

    @Inject
    WorkloadsDetectedOSTypeService workloadsDetectedOSTypeService;

    @Inject
    ComplexityService complexityService;

    @Inject
    RecommendedTargetsIMSService recommendedTargetsIMSService;

    @Inject
    WorkloadService workloadService;

    @Inject
    FlagService flagService;

    @Inject
    ScanRunService scanRunService;

    @Inject
    SummaryService summaryService;

    @Inject
    OSInformationService osInformationService;

    @Inject
    WorkloadsJavaRuntimeDetectedService workloadsJavaRuntimeDetectedService;

    @Inject
    WorkloadsApplicationPlatformsDetectedService workloadsApplicationPlatformsDetectedService;

    @Override
    public void configure() throws Exception {
        super.configure();
        from("direct:calculate-workloadsummaryreportmodel")
            .routeId("calculate-workloadsummaryreportmodel")
            .process(exchange -> {
                Long analysisId = Long.parseLong(((Map<String, String>) exchange.getIn().getHeader(MA_METADATA)).get(ANALYSIS_ID));
                WorkloadSummaryReportModel workloadSummaryReportModel = new WorkloadSummaryReportModel();

                //retrieve each model one after the other
                List<SummaryModel> summaryModels = summaryService.calculateSummaryModels(analysisId);
                // Set the components into the WorkloadSummaryReportModel bean
                workloadSummaryReportModel.setSummaryModels(new LinkedHashSet<>(summaryModels)); // LinkedHashSet to preserve the order

                // Calculate the other parts of the Workload Summary Report
                // and set them into the workloadSummaryReportModel bean
                ComplexityModel complexityModel = complexityService.calculateComplexityModels(analysisId);
                workloadSummaryReportModel.setComplexityModel(complexityModel);

                RecommendedTargetsIMSModel recommendedTargetsIMSModel = recommendedTargetsIMSService.calculateRecommendedTargetsIMS(analysisId);
                workloadSummaryReportModel.setRecommendedTargetsIMSModel(recommendedTargetsIMSModel);

                List<WorkloadModel> workloadModels = workloadService.calculateWorkloadsModels(analysisId);
                workloadSummaryReportModel.setWorkloadModels(workloadModels);

                List<FlagModel> flagModels = flagService.calculateFlagModels(analysisId);
                workloadSummaryReportModel.setFlagModels(flagModels);

                Set<ScanRunModel> scanRunModels = scanRunService.calculateScanRunModels(analysisId);
                workloadSummaryReportModel.setScanRunModels(scanRunModels);

                // Set the WorkloadSummaryReportModel into the AnalysisModel
                analysisService.setWorkloadSummaryReportModel(workloadSummaryReportModel, analysisId);

                // Refresh the workloadSummaryReportModel
                AnalysisModel analysisModel = analysisService.findByOwnerAndId(exchange.getIn().getHeader(USERNAME, String.class), analysisId);
                workloadSummaryReportModel = analysisModel.getWorkloadSummaryReportModels();

                // Calculate parts of the Workload Summary Report which depends of previous data
                List<WorkloadsDetectedOSTypeModel> workloadsDetectedOSTypeModels = workloadsDetectedOSTypeService.calculateWorkloadsDetectedOSTypeModels(analysisId);
                workloadSummaryReportModel.setWorkloadsDetectedOSTypeModels(new LinkedHashSet<>(workloadsDetectedOSTypeModels)); // LinkedHashSet to preserve the order

                List<OSInformationModel> osInformationModels = osInformationService.calculateOSFamiliesModels(analysisId);
                workloadSummaryReportModel.setOsInformation(new LinkedHashSet<>(osInformationModels)); // LinkedList to preserve the order

                List<WorkloadsJavaRuntimeDetectedModel> workloadsJavaRuntimeDetectedModels = workloadsJavaRuntimeDetectedService.calculateWorkloadsJavaRuntimeDetectedModels(analysisId);
                workloadSummaryReportModel.setJavaRuntimes(new LinkedHashSet<>(workloadsJavaRuntimeDetectedModels)); // LinkedList to preserve the order

                List<WorkloadsApplicationPlatformsDetectedModel> workloadsApplicationPlatformsDetectedModels = workloadsApplicationPlatformsDetectedService.calculateWorkloadApplicationPlatformsDetectedModels(analysisId);
                workloadSummaryReportModel.setApplicationPlatforms(new LinkedHashSet<>(workloadsApplicationPlatformsDetectedModels)); // LinkedList to preserve the order

                // Set the WorkloadSummaryReportModel into the AnalysisModel and update status to CREATED
                analysisService.setWorkloadSummaryReportModelAndUpdateStatus(workloadSummaryReportModel, analysisId);
            });
    }
}

package org.jboss.xavier.integrations.jpa.service;

import org.jboss.xavier.analytics.pojo.output.AnalysisModel;
import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.integrations.jpa.repository.AnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnalysisService
{
    @Autowired
    AnalysisRepository analysisRepository;

    public AnalysisModel findById(Long id)
    {
        return analysisRepository.findOne(id);
    }

    public void deleteById(Long id)
    {
        analysisRepository.delete(id);
    }
    
    public AnalysisModel buildAndSave(String reportName, String reportDescription, String payloadName) {
        AnalysisModel analysisModel = new AnalysisModel();
        analysisModel.setPayloadName(payloadName);
        analysisModel.setReportDescription(reportDescription);
        analysisModel.setReportName(reportName);
        return analysisRepository.saveAndFlush(analysisModel);
    }
    
    public void setInitialSavingsEstimationReportModel(InitialSavingsEstimationReportModel reportModel, Long id) {
        AnalysisModel analysisModel = findById(id);
        analysisModel.setInitialSavingsEstimationReportModel(reportModel);
        analysisRepository.saveAndFlush(analysisModel);
    }
    
    public void addWorkloadInventoryReportModel(WorkloadInventoryReportModel reportModel, Long id) {
        AnalysisModel analysisModel = findById(id);
        analysisModel.addWorkloadInventoryReportModel(reportModel);
        analysisRepository.saveAndFlush(analysisModel);        
    }
}

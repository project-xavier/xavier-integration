package org.jboss.xavier.integrations.jpa.service;

import org.jboss.xavier.analytics.pojo.output.workload.summary.WorkloadsJavaRuntimeDetectedModel;
import org.jboss.xavier.integrations.jpa.repository.WorkloadsJavaRuntimeDetectedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkloadsJavaRuntimeDetectedService
{
    @Autowired
    WorkloadsJavaRuntimeDetectedRepository workloadsJavaRuntimeDetectedRepository;

    public List<WorkloadsJavaRuntimeDetectedModel> calculateWorkloadsJavaRuntimeDetectedModels(Long analysisId)
    {
        return workloadsJavaRuntimeDetectedRepository.calculateWorkloadsJavaRuntimeDetectedModels(analysisId);
    }
}

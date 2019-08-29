package org.jboss.xavier.integrations.jpa.service;

import org.jboss.xavier.analytics.pojo.output.workload.summary.WorkloadsDetectedModel;
import org.jboss.xavier.integrations.jpa.repository.WorkloadsDetectedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkloadsDetectedService
{
    @Autowired
    WorkloadsDetectedRepository workloadsDetectedRepository;

    public WorkloadsDetectedModel calculateWorkloadsDetectedModels(Long analysisId)
    {
        return workloadsDetectedRepository.calculateWorkloadsDetectedModels(analysisId);
    }
}

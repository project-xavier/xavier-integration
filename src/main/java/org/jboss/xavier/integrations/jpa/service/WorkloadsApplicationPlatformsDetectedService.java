package org.jboss.xavier.integrations.jpa.service;

import org.jboss.xavier.analytics.pojo.output.workload.summary.WorkloadsApplicationPlatformsDetectedModel;
import org.jboss.xavier.integrations.jpa.repository.WorkloadsApplicationPlatformsDetectedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkloadsApplicationPlatformsDetectedService
{
    @Autowired
    WorkloadsApplicationPlatformsDetectedRepository workloadsApplicationPlatformsDetectedRepository;

    public List<WorkloadsApplicationPlatformsDetectedModel> calculateWorkloadApplicationPlatformsDetectedModels(Long analysisId)
    {
        return workloadsApplicationPlatformsDetectedRepository.calculateWorkloadApplicationPlatformsDetectedModels(analysisId);
    }
}

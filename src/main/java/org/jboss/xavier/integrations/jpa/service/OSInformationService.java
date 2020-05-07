package org.jboss.xavier.integrations.jpa.service;

import org.jboss.xavier.analytics.pojo.output.workload.summary.OSInformationModel;
import org.jboss.xavier.integrations.jpa.repository.OSInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OSInformationService
{
    @Autowired
    OSInformationRepository osInformationRepository;

    public List<OSInformationModel> calculateOSFamiliesModels(Long analysisId)
    {
        return osInformationRepository.calculateOSFamiliesModels(analysisId);
    }
}

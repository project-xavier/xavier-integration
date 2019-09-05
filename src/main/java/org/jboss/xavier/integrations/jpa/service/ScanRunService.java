package org.jboss.xavier.integrations.jpa.service;

import org.jboss.xavier.analytics.pojo.output.workload.summary.ScanRunModel;
import org.jboss.xavier.integrations.jpa.repository.ScanRunRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScanRunService {
    @Autowired
    ScanRunRepository scanRunRepository;

    public List<ScanRunModel> calculateScanRunModels(Long analysisId)
    {
        return scanRunRepository.calculateScanRunModels(analysisId);
    }

}

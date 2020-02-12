package org.jboss.xavier.integrations.migrationanalytics.business.issuehandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnalysisIssuesHandlerLogging implements AnalysisIssuesHandler {
    @Value("${analysis.dataintegrity.log:true}")
    boolean analysisDataIntegrityLog;

    @Override
    public void record(String analysisId, String entity, String entityName, String jsonPath, String message) {
        if (analysisDataIntegrityLog) {
            log.warn("Exception on {} [{}] reading value from JSON [{}}] : {}", entity, entityName, jsonPath, message);
        }
    }
}

package org.jboss.xavier.integrations.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.jboss.xavier.integrations.jpa.service.AnalysisService;

import javax.inject.Inject;
import java.util.Map;

import static org.jboss.xavier.integrations.route.MainRouteBuilder.ANALYSIS_ID;
import static org.jboss.xavier.integrations.route.MainRouteBuilder.MA_METADATA;

public abstract class RouteBuilderExceptionHandler extends RouteBuilder {
    @Inject
    protected AnalysisService analysisService;

    @Override
    public void configure() throws Exception {
        onException(Exception.class).routeId("exception-handler")
                .handled(true)
                .process(this::markAnalysisAsFailed)
                .stop();
    }

    public void markAnalysisAsFailed(Exchange e) {
        try {
            String analysisId = e.getIn().getHeader(ANALYSIS_ID, "", String.class);
            if (analysisId.isEmpty()) {
                analysisId = (String) e.getIn().getHeader(MA_METADATA, Map.class).get(ANALYSIS_ID);
            }
            analysisService.markAsFailedIfNotCreated(Long.parseLong(analysisId));
        } catch (Exception ex) {
            log.error("Exception ocurred while marking the Analysis as failed.", ex);
        }
    }
}

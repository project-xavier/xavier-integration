package org.jboss.xavier.integrations.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jboss.xavier.integrations.jpa.service.AnalysisService;

import javax.inject.Inject;
import java.util.Map;

public abstract class RouteBuilderExceptionHandler extends RouteBuilder {
    public static final String UPLOADFORMDATA = "uploadformdata";
    public static final String MA_METADATA = "MA_metadata";
    public static final String ANALYSIS_ID = "analysisId";
    public static final String USERNAME = "analysisUsername";
    public static final String USER_ACCOUNT_NUMBER = "analysisUserAccountNumber";
    public static final String CORRELATION_ID = "correlationId";
    public static final String WORKING_DIR = "workingFile";
    public static final String WORKING_FILE = "workingFile";
    public static final String FROM_DATE = "fromDate";
    public static final String TO_DATE = "toDate";

    public static final String X_RH_IDENTITY = "x-rh-identity";
    public static final String X_RH_IDENTITY_JSON_NODE = "x-rh-identity-json-node";

    @Inject
    protected AnalysisService analysisService;

    @Override
    public void configure() throws Exception {
        onException(Exception.class).routeId("exception-handler")
                .handled(true)
                .setHeader("exception", simple("${exception.stacktrace}"))
                .process(this::markAnalysisAsFailed)
                .stop();
    }

    public void markAnalysisAsFailed(Exchange e) {
        System.out.println("++++++++++++++ EXCEPTION : " + e);
        String analysisId = "";
        try {
             analysisId = e.getIn().getHeader(ANALYSIS_ID, "", String.class);
            if (analysisId.isEmpty() && e.getIn().getHeader(MA_METADATA, Map.class) != null) {
                analysisId = (String) e.getIn().getHeader(MA_METADATA, Map.class).get(ANALYSIS_ID);
            }
            if (StringUtils.isNotEmpty(analysisId)) {
                analysisService.markAsFailedIfNotCreated(Long.parseLong(analysisId));
            }
            log.error("Exception occurred while running the Analysis [{}] \n {} ", analysisId, e.getIn().getHeader("exception", String.class));
            e.getIn().setHeader("exception", null);
        } catch (Exception ex) {
            log.error("Exception occurred while marking the Analysis [" + analysisId + "] as failed.", ex);
        }
    }
}

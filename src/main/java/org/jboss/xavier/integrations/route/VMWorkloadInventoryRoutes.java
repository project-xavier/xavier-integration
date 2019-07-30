package org.jboss.xavier.integrations.route;

import org.apache.camel.builder.RouteBuilder;
import org.jboss.xavier.integrations.migrationanalytics.business.VMWorkloadInventoryCalculator;

import javax.inject.Named;

@Named
public class VMWorkloadInventoryRoutes extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:calculate-vmworkloadinventory")
                .id("calculate-vmworkloadinventory")
                .doTry()
                    .convertBodyTo(String.class)
                    .transform().method(VMWorkloadInventoryCalculator.class, "calculate(${body}, ${header.MA_metadata})")
                    .to("jms:queue:vm-workload-inventory")
                .endDoTry()
                .doCatch(Exception.class)
                    .to("log:error?showCaughtException=true&showStackTrace=true")
                    .setBody(simple("Exception on parsing Cloudforms file"))
                .end();
        
        from ("jms:queue:vm-workload-inventory")
            .to("log:INFO?showBody=true&showHeaders=true")
            .transform().method("decisionServerHelper", "createMigrationAnalyticsCommand(${body})")
            .to("direct:decisionserver")
            .transform().method("decisionServerHelper", "extractInitialSavingsEstimationReportModel")
            .transform().method("analysisModel", "setInitialSavingsEstimationReportModel(${body})")
            .setBody().simple("${ref:analysisModel}")
            .to("jpa:org.jboss.xavier.analytics.pojo.output.AnalysisModel");
            
    }
}

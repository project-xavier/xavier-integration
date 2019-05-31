package org.jboss.xavier.integrations.migrationanalytics.output;

import org.jboss.xavier.integrations.migrationanalytics.input.InputDataModel;
import org.jboss.xavier.integrations.route.model.cloudforms.CloudFormAnalysis;

public class AnalyticsCalculator {
    public InputDataModel calculate(CloudFormAnalysis cloudFormAnalysis, String customerid, String filename) {
        int numberofhosts = cloudFormAnalysis.getDatacenters()
                .stream()
                .flatMap(e -> e.getEmsClusters().stream())
                .mapToInt(t -> t.getHosts().size())
                .sum();
        long totalspace = cloudFormAnalysis.getDatacenters()
                .stream()
                .flatMap(e-> e.getDatastores().stream())
                .mapToLong(t -> t.getTotalSpace())
                .sum();
        return InputDataModel.builder().customerId(customerid).fileName(filename).numberOfHosts(numberofhosts).totalDiskSpace(totalspace).build();
    }
}

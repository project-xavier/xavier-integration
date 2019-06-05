package org.jboss.xavier.integrations.migrationanalytics.output;

import org.jboss.xavier.integrations.migrationanalytics.input.InputDataModel;
import org.jboss.xavier.integrations.route.model.cloudforms.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyticsCalculatorTest {

    @Test
    void calculate() {
        // Given
        AnalyticsCalculator analyticsCalculator = new AnalyticsCalculator();
        String filename = "ficherito.json";
        String customerid = "CIDE9988";
        List<Datastore> datastores = Arrays.asList(Datastore.builder().totalSpace(1000L).build(), Datastore.builder().totalSpace(3000L).build());
        List<EmsCluster> emsClusters = Arrays.asList(EmsCluster.builder().hosts(Arrays.asList(Host.builder().name("host1").build(),Host.builder().name("host2").build())).build(),
                                                     EmsCluster.builder().hosts(Arrays.asList(Host.builder().name("host3").build(),Host.builder().name("host4").build())).build());
        Datacenter dc1 = Datacenter.builder().name("DC1").datastores(datastores).emsClusters(emsClusters).build();
        Datacenter dc2 = Datacenter.builder().name("DC2").build();
        List<Datacenter> datacenters = new ArrayList<>(Arrays.asList(dc1, dc2));
        CloudFormAnalysis cloudFormAnalysis = CloudFormAnalysis.builder().hostname("HOST123").apiVersion("1.0").datacenters(datacenters).build();
        
        // When
        InputDataModel inputDataModelCalculated = analyticsCalculator.calculate(cloudFormAnalysis, customerid, filename);
        
        // Then
        // positive condition
        assertThat(InputDataModel.builder().customerId(customerid).fileName(filename).numberOfHosts(4).totalDiskSpace(4000L).build()).isEqualToComparingFieldByFieldRecursively(inputDataModelCalculated);
        // negative condition
        assertThat(Collections.singleton(InputDataModel.builder().customerId(customerid).fileName(filename).numberOfHosts(5).totalDiskSpace(5000L).build())).noneSatisfy(element -> assertThat(element).isEqualToComparingFieldByFieldRecursively(inputDataModelCalculated));
    }
}
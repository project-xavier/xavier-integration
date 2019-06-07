package org.jboss.xavier.integrations.migrationanalytics.output;

import org.jboss.xavier.integrations.migrationanalytics.input.InputDataModel;
import org.jboss.xavier.integrations.route.model.cloudforms.CloudFormAnalysis;
import org.jboss.xavier.integrations.route.model.cloudforms.Datacenter;
import org.jboss.xavier.integrations.route.model.cloudforms.Datastore;
import org.jboss.xavier.integrations.route.model.cloudforms.EmsCluster;
import org.jboss.xavier.integrations.route.model.cloudforms.Host;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AnalyticsCalculatorTest {

    @Test
    public void analyticsCalculator_calculate_CloudFormsModelWith8HostsAnd8000OfSpaceGiven_ShouldReturn8HostsAnd8000TotalDiskSpace() {
        // Given
        String filename = "ficherito.json";
        String customerid = "CIDE9988";
        
        List<Datastore> datastores = Arrays.asList(Datastore.builder().totalSpace(1000L).build(), Datastore.builder().totalSpace(3000L).build());
        
        List<EmsCluster> emsClusters = Arrays.asList(EmsCluster.builder().hosts(Arrays.asList(Host.builder().name("host1").build(),Host.builder().name("host2").build())).build(),
                                                     EmsCluster.builder().hosts(Arrays.asList(Host.builder().name("host3").build(),Host.builder().name("host4").build())).build());

        Datacenter dc1 = Datacenter.builder().name("DC1").datastores(datastores).emsClusters(emsClusters).build();
        Datacenter dc2 = Datacenter.builder().name("DC2").datastores(datastores).emsClusters(emsClusters).build();
        List<Datacenter> datacenters = Arrays.asList(dc1, dc2);

        CloudFormAnalysis cloudFormAnalysis = CloudFormAnalysis.builder().hostname("HOST123").apiVersion("1.0").datacenters(datacenters).build();

        // When
        AnalyticsCalculator analyticsCalculator = new AnalyticsCalculator();
        InputDataModel inputDataModelCalculated = analyticsCalculator.calculate(cloudFormAnalysis, customerid, filename);
        
        // Then
        // positive condition
        assertThat(InputDataModel.builder().customerId(customerid).fileName(filename).numberOfHosts(8).totalDiskSpace(8000L).build()).isEqualToComparingFieldByFieldRecursively(inputDataModelCalculated);
        // negative condition
        assertThat(Collections.singleton(InputDataModel.builder().customerId(customerid).fileName(filename).numberOfHosts(5).totalDiskSpace(5000L).build())).noneSatisfy(element -> assertThat(element).isEqualToComparingFieldByFieldRecursively(inputDataModelCalculated));
    }
}
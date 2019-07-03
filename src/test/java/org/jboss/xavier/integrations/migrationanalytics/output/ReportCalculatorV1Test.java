package org.jboss.xavier.integrations.migrationanalytics.output;

import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel;
import org.jboss.xavier.integrations.Application;
import org.jboss.xavier.integrations.route.model.cloudforms.Calculator;
import org.jboss.xavier.integrations.route.model.cloudforms.v1.CloudFormsExport;
import org.jboss.xavier.integrations.route.model.cloudforms.v1.EmsCluster;
import org.jboss.xavier.integrations.route.model.cloudforms.v1.Host;
import org.jboss.xavier.integrations.route.model.cloudforms.v1.ManageIQProvidersVmwareInfraManager;
import org.jboss.xavier.integrations.route.model.cloudforms.v1.Storage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {Application.class})
@UseAdviceWith // Disables automatic start of Camel context
@ActiveProfiles("test")
public class ReportCalculatorV1Test {
    
    @Value("${cloudforms.filter.type}")
    private String cloudformsFilterType;

    @Inject @Named("analyticsCalculatorV1")
    private Calculator reportCalculatorV1;

    @Test
    public void analyticsCalculator_calculate_CloudFormsModelWith8HostsAnd8000OfSpaceGiven_ShouldReturn8HostsAnd8000TotalDiskSpace() {
        // Given
        String filename = "cloudforms-export-v1.json";
        String customerid = "CIDE9988";
        
        List<EmsCluster> emsClusters = Arrays.asList(
                new EmsCluster().withHosts(Arrays.asList(
                    new Host().withName("host1").withType(cloudformsFilterType).withStorages(Arrays.asList(
                            new Storage().withTotalSpace(1000L), 
                            new Storage().withTotalSpace(2000L))).withCpuTotalCores(24L).withCpuCoresPerSocket(4L),
                    new Host().withName("host2").withType(cloudformsFilterType).withStorages(Arrays.asList(
                            new Storage().withTotalSpace(500L),
                            new Storage().withTotalSpace(300L))).withCpuTotalCores(24L).withCpuCoresPerSocket(4L))),
                new EmsCluster().withHosts(Arrays.asList(
                    new Host().withName("host3").withType(cloudformsFilterType).withStorages(Arrays.asList(
                            new Storage().withTotalSpace(50L),
                            new Storage().withTotalSpace(10L))).withCpuTotalCores(24L).withCpuCoresPerSocket(4L),
                    new Host().withName("host4").withType("fake").withStorages(Arrays.asList(
                            new Storage().withTotalSpace(3L),
                            new Storage().withTotalSpace(2L))).withCpuTotalCores(24L).withCpuCoresPerSocket(4L))));

        CloudFormsExport cloudFormAnalysis = new CloudFormsExport().withManageIQProvidersVmwareInfraManager(new ManageIQProvidersVmwareInfraManager().withHostname("HOST123").withApiVersion("1.0").withEmsClusters(emsClusters));

        // When
        Integer sourceproductindicator = 1;
        Double year1hypervisorpercentage = 10D;
        Double year2hypervisorpercentage = 20D;
        Double year3hypervisorpercentage = 30D;
        Double growthratepercentage = 7D;
        Map<String, Object> headers = new HashMap<>();
        headers.put("filename", filename);
        headers.put("customerid", customerid);
        headers.put("sourceproductindicator", sourceproductindicator);
        headers.put("year1hypervisorpercentage", year1hypervisorpercentage);
        headers.put("year2hypervisorpercentage", year2hypervisorpercentage);
        headers.put("year3hypervisorpercentage", year3hypervisorpercentage);
        headers.put("growthratepercentage", growthratepercentage);
        UploadFormInputDataModel inputDataModelCalculated = reportCalculatorV1.calculate(cloudFormAnalysis, headers);

        // Then
        Integer hypervisor = 9;
        Long totaldiskspace = 3860L;
        UploadFormInputDataModel uploadFormInputDataModel = new UploadFormInputDataModel(customerid, filename, hypervisor, totaldiskspace, sourceproductindicator, year1hypervisorpercentage, year2hypervisorpercentage, year3hypervisorpercentage, growthratepercentage);
        assertThat(uploadFormInputDataModel).isEqualToComparingFieldByFieldRecursively(inputDataModelCalculated);
    }
}
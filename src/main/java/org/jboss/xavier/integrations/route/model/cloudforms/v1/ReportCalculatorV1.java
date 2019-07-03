package org.jboss.xavier.integrations.route.model.cloudforms.v1;

import org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel;
import org.jboss.xavier.integrations.route.model.cloudforms.Calculator;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;
import java.util.Map;

@Named("analyticsCalculatorV1")
public class ReportCalculatorV1 implements Calculator {
    @Value("${cloudforms.filter.type}")
    private String hostFilterByType;

    @Override
    public UploadFormInputDataModel calculate(CloudFormsExport cloudFormAnalysis, Map<String, Object> headers) { 
        Long numberofhypervisors = cloudFormAnalysis.getManageIQProvidersVmwareInfraManager()
                .getEmsClusters().stream()
                .flatMap(e -> e.getHosts().stream())
                .filter(j -> j.getType().equalsIgnoreCase(hostFilterByType))
                .mapToLong(e -> (e.getCpuTotalCores() / (e.getCpuCoresPerSocket() * 2)))
                .sum();
        
        Long totalspace = cloudFormAnalysis.getManageIQProvidersVmwareInfraManager()
                .getEmsClusters().stream()
                .flatMap(e -> e.getHosts().stream())
                .filter(j -> j.getType().equalsIgnoreCase(hostFilterByType))
                .flatMap(e -> e.getStorages().stream())
                .mapToLong(e -> e.getTotalSpace())
                .sum();

        String customerid = headers.get(Calculator.CUSTOMERID).toString();
        String filename = headers.get(Calculator.FILENAME).toString();
        int sourceproductindicator = Integer.parseInt(headers.get(Calculator.SOURCEPRODUCTINDICATOR) != null ? headers.get(Calculator.SOURCEPRODUCTINDICATOR).toString() : "0");
        double year1hypervisorpercentage = Double.parseDouble(headers.get(Calculator.YEAR_1_HYPERVISORPERCENTAGE) != null ? headers.get(Calculator.YEAR_1_HYPERVISORPERCENTAGE).toString() : "0");
        double year2hypervisorpercentage = Double.parseDouble(headers.get(Calculator.YEAR_2_HYPERVISORPERCENTAGE) != null ? headers.get(Calculator.YEAR_2_HYPERVISORPERCENTAGE).toString() : "0");
        double year3hypervisorpercentage = Double.parseDouble(headers.get(Calculator.YEAR_3_HYPERVISORPERCENTAGE) != null ? headers.get(Calculator.YEAR_3_HYPERVISORPERCENTAGE).toString() : "0");
        double growthratepercentage = Double.parseDouble(headers.get(Calculator.GROWTHRATEPERCENTAGE) != null ? headers.get(Calculator.GROWTHRATEPERCENTAGE).toString() : "0");
        
        return new UploadFormInputDataModel(customerid, filename, numberofhypervisors.intValue(), totalspace,
                sourceproductindicator, year1hypervisorpercentage,
                year2hypervisorpercentage,
                year3hypervisorpercentage, growthratepercentage);
    }
}
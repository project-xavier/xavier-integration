package org.jboss.xavier.integrations.migrationanalytics.business;

import com.jayway.jsonpath.JsonPath;
import org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named("calculator")
public class ParamsCalculator implements Calculator {
    @Inject
    private Environment env;
    
    @Value("${cloudforms.filter.type}")
    private String hostFilterByType;

    @Override
    public UploadFormInputDataModel calculate(String cloudFormsJson, Map<String, Object> headers) {
        String payloadVersion = getManifestVersion(cloudFormsJson);
        
        String cpuTotalCoresPath = env.getProperty("cloudforms.manifest." + payloadVersion + ".cpuTotalCoresPath", env.getProperty("cloudforms.manifest.v1.cpuTotalCoresPath"));
        String cpuCoresPerSocketPath = env.getProperty("cloudforms.manifest." + payloadVersion + ".cpuCoresPerSocketPath", env.getProperty("cloudforms.manifest.v1.cpuCoresPerSocketPath"));
        String totalSpacePath = env.getProperty("cloudforms.manifest." + payloadVersion + ".totalSpacePath", env.getProperty("cloudforms.manifest.v1.totalSpacePath"));

        // Calculations
        Integer cpuTotalCores = ((List<Integer>) JsonPath.read(cloudFormsJson, cpuTotalCoresPath)).stream().mapToInt(Integer::intValue).sum();
        Integer cpuCoresPerSocket = ((List<Integer>) JsonPath.read(cloudFormsJson, cpuCoresPerSocketPath)).stream().mapToInt(Integer::intValue).sum();
        Long totalspace = ((List<Long>) JsonPath.read(cloudFormsJson, totalSpacePath)).stream().mapToLong(Long::longValue).sum();

        Long numberofhypervisors = new Double(cpuTotalCores / (cpuCoresPerSocket * 2)).longValue();
        
        // User properties
        String customerid = headers.get(Calculator.CUSTOMERID).toString();
        String filename = headers.get(Calculator.FILENAME).toString();
        int sourceproductindicator = Integer.parseInt(headers.get(Calculator.SOURCEPRODUCTINDICATOR) != null ? headers.get(Calculator.SOURCEPRODUCTINDICATOR).toString() : "0");
        double year1hypervisorpercentage = Double.parseDouble(headers.get(Calculator.YEAR_1_HYPERVISORPERCENTAGE) != null ? headers.get(Calculator.YEAR_1_HYPERVISORPERCENTAGE).toString() : "0");
        double year2hypervisorpercentage = Double.parseDouble(headers.get(Calculator.YEAR_2_HYPERVISORPERCENTAGE) != null ? headers.get(Calculator.YEAR_2_HYPERVISORPERCENTAGE).toString() : "0");
        double year3hypervisorpercentage = Double.parseDouble(headers.get(Calculator.YEAR_3_HYPERVISORPERCENTAGE) != null ? headers.get(Calculator.YEAR_3_HYPERVISORPERCENTAGE).toString() : "0");
        double growthratepercentage = Double.parseDouble(headers.get(Calculator.GROWTHRATEPERCENTAGE) != null ? headers.get(Calculator.GROWTHRATEPERCENTAGE).toString() : "0");
        
        // Calculated and enriched model
        return new UploadFormInputDataModel(customerid, filename, numberofhypervisors.intValue(), totalspace,
                sourceproductindicator, year1hypervisorpercentage,
                year2hypervisorpercentage,
                year3hypervisorpercentage, growthratepercentage);
    }

    // It will try to extract the version of the payload from the JSON file, falling back to v1
    private String getManifestVersion(String cloudFormsJson) {
        return "v1";
    }
}
package org.jboss.xavier.integrations.route.model.cloudforms;

import org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel;
import org.jboss.xavier.integrations.route.model.cloudforms.v1.CloudFormsExport;

import java.util.Map;

public interface Calculator {
    String CUSTOMERID = "customerid";
    String FILENAME = "filename";
    String SOURCEPRODUCTINDICATOR = "sourceproductindicator";
    String YEAR_1_HYPERVISORPERCENTAGE = "year1hypervisorpercentage";
    String YEAR_2_HYPERVISORPERCENTAGE = "year2hypervisorpercentage";
    String YEAR_3_HYPERVISORPERCENTAGE = "year3hypervisorpercentage";
    String GROWTHRATEPERCENTAGE = "growthratepercentage";

    UploadFormInputDataModel calculate(CloudFormsExport cloudFormAnalysis, Map<String, Object> headers);
}

package org.jboss.xavier.integrations.migrationanalytics.business;

import org.jboss.xavier.analytics.pojo.input.VMWorkloadInventoryModel;

import javax.inject.Named;
import java.util.Map;

@Named
public class VMWorkloadInventoryCalculator implements Calculator<VMWorkloadInventoryModel> {
    
    @Override
    public VMWorkloadInventoryModel calculate(String cloudFormsJson, Map<String, Object> headers) {
        return null;
    }
}

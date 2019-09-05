package org.jboss.xavier.integrations.route.strategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;
import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;

public class WorkloadInventoryReportModelAggregationStrategy extends AbstractListAggregationStrategy<WorkloadInventoryReportModel>
{
    @Override
    public WorkloadInventoryReportModel getValue(Exchange exchange)
    {
        System.out.println("@@@@@@@ WorkloadInventoryReportModelAggregationStrategy.getValue " + exchange.getIn().getBody(WorkloadInventoryReportModel.class));
        return exchange.getIn().getBody(WorkloadInventoryReportModel.class);
    }
}

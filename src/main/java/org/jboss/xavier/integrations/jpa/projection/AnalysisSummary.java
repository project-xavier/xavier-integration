package org.jboss.xavier.integrations.jpa.projection;

import java.util.Date;

public interface AnalysisSummary
{
    Long getId();
    String getReportName();
    String getReportDescription();
    String getPayloadName();
    Date getInserted();
    Date getLastUpdate();
    String getStatus();
}

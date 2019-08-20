package org.jboss.xavier.analytics.pojo.output.workload.summary;

import java.util.Date;

public class ScanRunModel {

    private Long id;

    private Date date;
    private String target;
    private String type;

    ScanRunModel(){}

    ScanRunModel(Date date, String target, String type){
        this.date = date;
        this.target = target;
        this.type = type;
    }
}

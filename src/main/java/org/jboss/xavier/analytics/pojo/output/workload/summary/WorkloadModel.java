package org.jboss.xavier.analytics.pojo.output.workload.summary;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        indexes = {
                @Index(name = "WorkloadModel_" +
                        WorkloadModel.REPORT_ID + "_index",
                        columnList = WorkloadModel.REPORT_ID, unique = false),
                @Index(name = "WorkloadModel_" +
                        WorkloadModel.WORKLOAD + "_index",
                        columnList = WorkloadModel.WORKLOAD, unique = false),
                @Index(name = "WorkloadModel_" +
                        WorkloadModel.OS_NAME + "_index",
                        columnList = WorkloadModel.OS_NAME, unique = false),
                @Index(name = "WorkloadModel_" +
                        WorkloadModel.VMS + "_index",
                        columnList = WorkloadModel.VMS, unique = false)
        }
)
public class WorkloadModel {

    public static final String DEFAULT_SORT_FIELD = "id";
    public static final Set<String> SUPPORTED_SORT_FIELDS = new HashSet<>(
            Arrays.asList(WorkloadModel.DEFAULT_SORT_FIELD, WorkloadModel.WORKLOAD, WorkloadModel.OS_NAME, WorkloadModel.VMS)
    );

    static final String REPORT_ID = "report_id";
    static final String WORKLOAD = "workload";
    static final String OS_NAME = "osName";
    static final String VMS = "vms";

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = REPORT_ID)
    @JsonBackReference
    private WorkloadSummaryReportModel report;

    private String workload;
    private String osName;
    private Integer clusters;
    private Integer vms;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkloadSummaryReportModel getReport() {
        return report;
    }

    public void setReport(WorkloadSummaryReportModel report) {
        this.report = report;
    }

    public String getWorkload() {
        return workload;
    }

    public void setWorkload(String workload) {
        this.workload = workload;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public Integer getClusters() {
        return clusters;
    }

    public void setClusters(Integer clusters) {
        this.clusters = clusters;
    }

    public Integer getVms() {
        return vms;
    }

    public void setVms(Integer vms) {
        this.vms = vms;
    }
}

package org.jboss.xavier.analytics.pojo.output.workload.summary;

public class WorkloadModel {

    private Long id;

    private String workload;
    private String osName;
    private Integer clusters;
    private Integer vms;

    public WorkloadModel() {}

    public WorkloadModel(String workload, String osName, Integer clusters, Integer vms) {
        this.workload = workload;
        this.osName = osName;
        this.clusters = clusters;
        this.vms = vms;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

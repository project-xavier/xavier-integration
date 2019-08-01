package org.jboss.xavier.integrations.route.model;

import java.util.Objects;
import java.util.Set;

public class WorkloadInventoryFilterBean {

    private String provider;
    private String datacenter;
    private String cluster;
    private String vmName;
    private String osName;
    private Set<String> workloads;
    private String complexity;
    private Set<String> recommendedTargetsIMS;
    private Set<String> flagsIMS;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public Set<String> getWorkloads() {
        return workloads;
    }

    public void setWorkloads(Set<String> workloads) {
        this.workloads = workloads;
    }

    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    public Set<String> getRecommendedTargetsIMS() {
        return recommendedTargetsIMS;
    }

    public void setRecommendedTargetsIMS(Set<String> recommendedTargetsIMS) {
        this.recommendedTargetsIMS = recommendedTargetsIMS;
    }

    public Set<String> getFlagsIMS() {
        return flagsIMS;
    }

    public void setFlagsIMS(Set<String> flagsIMS) {
        this.flagsIMS = flagsIMS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkloadInventoryFilterBean that = (WorkloadInventoryFilterBean) o;
        return Objects.equals(provider, that.provider) &&
                Objects.equals(datacenter, that.datacenter) &&
                Objects.equals(cluster, that.cluster) &&
                Objects.equals(vmName, that.vmName) &&
                Objects.equals(osName, that.osName) &&
                Objects.equals(workloads, that.workloads) &&
                Objects.equals(complexity, that.complexity) &&
                Objects.equals(recommendedTargetsIMS, that.recommendedTargetsIMS) &&
                Objects.equals(flagsIMS, that.flagsIMS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, datacenter, cluster, vmName, osName, workloads, complexity, recommendedTargetsIMS, flagsIMS);
    }
}

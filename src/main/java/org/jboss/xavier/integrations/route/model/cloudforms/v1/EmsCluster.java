
package org.jboss.xavier.integrations.route.model.cloudforms.v1;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "type",
    "drs_automation_level",
    "drs_enabled",
    "drs_migration_threshold",
    "ha_admit_control",
    "ha_enabled",
    "aggregate_cpu_speed",
    "aggregate_cpu_total_cores",
    "aggregate_disk_capacity",
    "aggregate_memory",
    "aggregate_physical_cpus",
    "aggregate_vm_cpus",
    "aggregate_vm_memory",
    "total_hosts",
    "total_miq_templates",
    "total_vms",
    "total_vms_and_templates",
    "v_cpu_vr_ratio",
    "v_parent_datacenter",
    "v_qualified_desc",
    "v_ram_vr_ratio",
    "hosts"
})
public class EmsCluster {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private Object type;
    @JsonProperty("drs_automation_level")
    private String drsAutomationLevel;
    @JsonProperty("drs_enabled")
    private Boolean drsEnabled;
    @JsonProperty("drs_migration_threshold")
    private Long drsMigrationThreshold;
    @JsonProperty("ha_admit_control")
    private Boolean haAdmitControl;
    @JsonProperty("ha_enabled")
    private Boolean haEnabled;
    @JsonProperty("aggregate_cpu_speed")
    private Long aggregateCpuSpeed;
    @JsonProperty("aggregate_cpu_total_cores")
    private Long aggregateCpuTotalCores;
    @JsonProperty("aggregate_disk_capacity")
    private Long aggregateDiskCapacity;
    @JsonProperty("aggregate_memory")
    private Long aggregateMemory;
    @JsonProperty("aggregate_physical_cpus")
    private Long aggregatePhysicalCpus;
    @JsonProperty("aggregate_vm_cpus")
    private Long aggregateVmCpus;
    @JsonProperty("aggregate_vm_memory")
    private Long aggregateVmMemory;
    @JsonProperty("total_hosts")
    private Long totalHosts;
    @JsonProperty("total_miq_templates")
    private Long totalMiqTemplates;
    @JsonProperty("total_vms")
    private Long totalVms;
    @JsonProperty("total_vms_and_templates")
    private Long totalVmsAndTemplates;
    @JsonProperty("v_cpu_vr_ratio")
    private Double vCpuVrRatio;
    @JsonProperty("v_parent_datacenter")
    private String vParentDatacenter;
    @JsonProperty("v_qualified_desc")
    private String vQualifiedDesc;
    @JsonProperty("v_ram_vr_ratio")
    private Double vRamVrRatio;
    @JsonProperty("hosts")
    private List<Host> hosts = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    public EmsCluster withId(Long id) {
        this.id = id;
        return this;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public EmsCluster withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("type")
    public Object getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Object type) {
        this.type = type;
    }

    public EmsCluster withType(Object type) {
        this.type = type;
        return this;
    }

    @JsonProperty("drs_automation_level")
    public String getDrsAutomationLevel() {
        return drsAutomationLevel;
    }

    @JsonProperty("drs_automation_level")
    public void setDrsAutomationLevel(String drsAutomationLevel) {
        this.drsAutomationLevel = drsAutomationLevel;
    }

    public EmsCluster withDrsAutomationLevel(String drsAutomationLevel) {
        this.drsAutomationLevel = drsAutomationLevel;
        return this;
    }

    @JsonProperty("drs_enabled")
    public Boolean getDrsEnabled() {
        return drsEnabled;
    }

    @JsonProperty("drs_enabled")
    public void setDrsEnabled(Boolean drsEnabled) {
        this.drsEnabled = drsEnabled;
    }

    public EmsCluster withDrsEnabled(Boolean drsEnabled) {
        this.drsEnabled = drsEnabled;
        return this;
    }

    @JsonProperty("drs_migration_threshold")
    public Long getDrsMigrationThreshold() {
        return drsMigrationThreshold;
    }

    @JsonProperty("drs_migration_threshold")
    public void setDrsMigrationThreshold(Long drsMigrationThreshold) {
        this.drsMigrationThreshold = drsMigrationThreshold;
    }

    public EmsCluster withDrsMigrationThreshold(Long drsMigrationThreshold) {
        this.drsMigrationThreshold = drsMigrationThreshold;
        return this;
    }

    @JsonProperty("ha_admit_control")
    public Boolean getHaAdmitControl() {
        return haAdmitControl;
    }

    @JsonProperty("ha_admit_control")
    public void setHaAdmitControl(Boolean haAdmitControl) {
        this.haAdmitControl = haAdmitControl;
    }

    public EmsCluster withHaAdmitControl(Boolean haAdmitControl) {
        this.haAdmitControl = haAdmitControl;
        return this;
    }

    @JsonProperty("ha_enabled")
    public Boolean getHaEnabled() {
        return haEnabled;
    }

    @JsonProperty("ha_enabled")
    public void setHaEnabled(Boolean haEnabled) {
        this.haEnabled = haEnabled;
    }

    public EmsCluster withHaEnabled(Boolean haEnabled) {
        this.haEnabled = haEnabled;
        return this;
    }

    @JsonProperty("aggregate_cpu_speed")
    public Long getAggregateCpuSpeed() {
        return aggregateCpuSpeed;
    }

    @JsonProperty("aggregate_cpu_speed")
    public void setAggregateCpuSpeed(Long aggregateCpuSpeed) {
        this.aggregateCpuSpeed = aggregateCpuSpeed;
    }

    public EmsCluster withAggregateCpuSpeed(Long aggregateCpuSpeed) {
        this.aggregateCpuSpeed = aggregateCpuSpeed;
        return this;
    }

    @JsonProperty("aggregate_cpu_total_cores")
    public Long getAggregateCpuTotalCores() {
        return aggregateCpuTotalCores;
    }

    @JsonProperty("aggregate_cpu_total_cores")
    public void setAggregateCpuTotalCores(Long aggregateCpuTotalCores) {
        this.aggregateCpuTotalCores = aggregateCpuTotalCores;
    }

    public EmsCluster withAggregateCpuTotalCores(Long aggregateCpuTotalCores) {
        this.aggregateCpuTotalCores = aggregateCpuTotalCores;
        return this;
    }

    @JsonProperty("aggregate_disk_capacity")
    public Long getAggregateDiskCapacity() {
        return aggregateDiskCapacity;
    }

    @JsonProperty("aggregate_disk_capacity")
    public void setAggregateDiskCapacity(Long aggregateDiskCapacity) {
        this.aggregateDiskCapacity = aggregateDiskCapacity;
    }

    public EmsCluster withAggregateDiskCapacity(Long aggregateDiskCapacity) {
        this.aggregateDiskCapacity = aggregateDiskCapacity;
        return this;
    }

    @JsonProperty("aggregate_memory")
    public Long getAggregateMemory() {
        return aggregateMemory;
    }

    @JsonProperty("aggregate_memory")
    public void setAggregateMemory(Long aggregateMemory) {
        this.aggregateMemory = aggregateMemory;
    }

    public EmsCluster withAggregateMemory(Long aggregateMemory) {
        this.aggregateMemory = aggregateMemory;
        return this;
    }

    @JsonProperty("aggregate_physical_cpus")
    public Long getAggregatePhysicalCpus() {
        return aggregatePhysicalCpus;
    }

    @JsonProperty("aggregate_physical_cpus")
    public void setAggregatePhysicalCpus(Long aggregatePhysicalCpus) {
        this.aggregatePhysicalCpus = aggregatePhysicalCpus;
    }

    public EmsCluster withAggregatePhysicalCpus(Long aggregatePhysicalCpus) {
        this.aggregatePhysicalCpus = aggregatePhysicalCpus;
        return this;
    }

    @JsonProperty("aggregate_vm_cpus")
    public Long getAggregateVmCpus() {
        return aggregateVmCpus;
    }

    @JsonProperty("aggregate_vm_cpus")
    public void setAggregateVmCpus(Long aggregateVmCpus) {
        this.aggregateVmCpus = aggregateVmCpus;
    }

    public EmsCluster withAggregateVmCpus(Long aggregateVmCpus) {
        this.aggregateVmCpus = aggregateVmCpus;
        return this;
    }

    @JsonProperty("aggregate_vm_memory")
    public Long getAggregateVmMemory() {
        return aggregateVmMemory;
    }

    @JsonProperty("aggregate_vm_memory")
    public void setAggregateVmMemory(Long aggregateVmMemory) {
        this.aggregateVmMemory = aggregateVmMemory;
    }

    public EmsCluster withAggregateVmMemory(Long aggregateVmMemory) {
        this.aggregateVmMemory = aggregateVmMemory;
        return this;
    }

    @JsonProperty("total_hosts")
    public Long getTotalHosts() {
        return totalHosts;
    }

    @JsonProperty("total_hosts")
    public void setTotalHosts(Long totalHosts) {
        this.totalHosts = totalHosts;
    }

    public EmsCluster withTotalHosts(Long totalHosts) {
        this.totalHosts = totalHosts;
        return this;
    }

    @JsonProperty("total_miq_templates")
    public Long getTotalMiqTemplates() {
        return totalMiqTemplates;
    }

    @JsonProperty("total_miq_templates")
    public void setTotalMiqTemplates(Long totalMiqTemplates) {
        this.totalMiqTemplates = totalMiqTemplates;
    }

    public EmsCluster withTotalMiqTemplates(Long totalMiqTemplates) {
        this.totalMiqTemplates = totalMiqTemplates;
        return this;
    }

    @JsonProperty("total_vms")
    public Long getTotalVms() {
        return totalVms;
    }

    @JsonProperty("total_vms")
    public void setTotalVms(Long totalVms) {
        this.totalVms = totalVms;
    }

    public EmsCluster withTotalVms(Long totalVms) {
        this.totalVms = totalVms;
        return this;
    }

    @JsonProperty("total_vms_and_templates")
    public Long getTotalVmsAndTemplates() {
        return totalVmsAndTemplates;
    }

    @JsonProperty("total_vms_and_templates")
    public void setTotalVmsAndTemplates(Long totalVmsAndTemplates) {
        this.totalVmsAndTemplates = totalVmsAndTemplates;
    }

    public EmsCluster withTotalVmsAndTemplates(Long totalVmsAndTemplates) {
        this.totalVmsAndTemplates = totalVmsAndTemplates;
        return this;
    }

    @JsonProperty("v_cpu_vr_ratio")
    public Double getVCpuVrRatio() {
        return vCpuVrRatio;
    }

    @JsonProperty("v_cpu_vr_ratio")
    public void setVCpuVrRatio(Double vCpuVrRatio) {
        this.vCpuVrRatio = vCpuVrRatio;
    }

    public EmsCluster withVCpuVrRatio(Double vCpuVrRatio) {
        this.vCpuVrRatio = vCpuVrRatio;
        return this;
    }

    @JsonProperty("v_parent_datacenter")
    public String getVParentDatacenter() {
        return vParentDatacenter;
    }

    @JsonProperty("v_parent_datacenter")
    public void setVParentDatacenter(String vParentDatacenter) {
        this.vParentDatacenter = vParentDatacenter;
    }

    public EmsCluster withVParentDatacenter(String vParentDatacenter) {
        this.vParentDatacenter = vParentDatacenter;
        return this;
    }

    @JsonProperty("v_qualified_desc")
    public String getVQualifiedDesc() {
        return vQualifiedDesc;
    }

    @JsonProperty("v_qualified_desc")
    public void setVQualifiedDesc(String vQualifiedDesc) {
        this.vQualifiedDesc = vQualifiedDesc;
    }

    public EmsCluster withVQualifiedDesc(String vQualifiedDesc) {
        this.vQualifiedDesc = vQualifiedDesc;
        return this;
    }

    @JsonProperty("v_ram_vr_ratio")
    public Double getVRamVrRatio() {
        return vRamVrRatio;
    }

    @JsonProperty("v_ram_vr_ratio")
    public void setVRamVrRatio(Double vRamVrRatio) {
        this.vRamVrRatio = vRamVrRatio;
    }

    public EmsCluster withVRamVrRatio(Double vRamVrRatio) {
        this.vRamVrRatio = vRamVrRatio;
        return this;
    }

    @JsonProperty("hosts")
    public List<Host> getHosts() {
        return hosts;
    }

    @JsonProperty("hosts")
    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    public EmsCluster withHosts(List<Host> hosts) {
        this.hosts = hosts;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public EmsCluster withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

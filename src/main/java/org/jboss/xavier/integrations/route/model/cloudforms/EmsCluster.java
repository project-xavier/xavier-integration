
package org.jboss.xavier.integrations.route.model.cloudforms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "aggregate_physical_cpus",
    "aggregate_cpu_total_cores",
    "aggregate_cpu_speed",
    "aggregate_memory",
    "effective_cpu",
    "effective_memory",
    "aggregate_vm_cpus",
    "aggregate_vm_memory",
    "drs_enabled",
    "drs_automation_level",
    "drs_migration_threshold",
    "ha_enabled",
    "ha_admit_control",
    "ha_max_failures",
    "total_direct_vms",
    "total_direct_miq_templates",
    "v_cpu_vr_ratio",
    "v_ram_vr_ratio",
    "hosts"
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmsCluster {

    @JsonProperty("name")
    private String name;
    @JsonProperty("aggregate_physical_cpus")
    private Long aggregatePhysicalCpus;
    @JsonProperty("aggregate_cpu_total_cores")
    private Long aggregateCpuTotalCores;
    @JsonProperty("aggregate_cpu_speed")
    private Long aggregateCpuSpeed;
    @JsonProperty("aggregate_memory")
    private Long aggregateMemory;
    @JsonProperty("effective_cpu")
    private Long effectiveCpu;
    @JsonProperty("effective_memory")
    private Long effectiveMemory;
    @JsonProperty("aggregate_vm_cpus")
    private Long aggregateVmCpus;
    @JsonProperty("aggregate_vm_memory")
    private Long aggregateVmMemory;
    @JsonProperty("drs_enabled")
    private Boolean drsEnabled;
    @JsonProperty("drs_automation_level")
    private String drsAutomationLevel;
    @JsonProperty("drs_migration_threshold")
    private Long drsMigrationThreshold;
    @JsonProperty("ha_enabled")
    private Boolean haEnabled;
    @JsonProperty("ha_admit_control")
    private Boolean haAdmitControl;
    @JsonProperty("ha_max_failures")
    private Long haMaxFailures;
    @JsonProperty("total_direct_vms")
    private Long totalDirectVms;
    @JsonProperty("total_direct_miq_templates")
    private Long totalDirectMiqTemplates;
    @JsonProperty("v_cpu_vr_ratio")
    private Double vCpuVrRatio;
    @JsonProperty("v_ram_vr_ratio")
    private Double vRamVrRatio;
    @JsonProperty("hosts")
    private List<Host> hosts = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

}


package org.jboss.xavier.integrations.route.model.cloudforms;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "power_state",
    "vmm_product",
    "vmm_version",
    "vmm_buildnumber",
    "num_cpu",
    "cpu_total_cores",
    "cpu_cores_per_socket",
    "hyperthreading",
    "ram_size",
    "v_total_vms",
    "v_total_miq_templates"
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Host {

    @JsonProperty("name")
    private String name;
    @JsonProperty("power_state")
    private String powerState;
    @JsonProperty("vmm_product")
    private String vmmProduct;
    @JsonProperty("vmm_version")
    private String vmmVersion;
    @JsonProperty("vmm_buildnumber")
    private String vmmBuildnumber;
    @JsonProperty("num_cpu")
    private Long numCpu;
    @JsonProperty("cpu_total_cores")
    private Long cpuTotalCores;
    @JsonProperty("cpu_cores_per_socket")
    private Long cpuCoresPerSocket;
    @JsonProperty("hyperthreading")
    private Boolean hyperthreading;
    @JsonProperty("ram_size")
    private Long ramSize;
    @JsonProperty("v_total_vms")
    private Long vTotalVms;
    @JsonProperty("v_total_miq_templates")
    private Long vTotalMiqTemplates;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

}

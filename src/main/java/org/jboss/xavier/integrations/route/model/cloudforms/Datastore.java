
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
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "store_type",
    "raw_disk_mappings_supported",
    "thin_provisioning_supported",
    "directory_hierarchy_supported",
    "multiplehostaccess",
    "total_space",
    "uncommitted",
    "free_space",
    "v_total_vms",
    "hosts",
    "storage_profiles"
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Datastore {

    @JsonProperty("name")
    private String name;
    @JsonProperty("store_type")
    private String storeType;
    @JsonProperty("raw_disk_mappings_supported")
    private Boolean rawDiskMappingsSupported;
    @JsonProperty("thin_provisioning_supported")
    private Boolean thinProvisioningSupported;
    @JsonProperty("directory_hierarchy_supported")
    private Boolean directoryHierarchySupported;
    @JsonProperty("multiplehostaccess")
    private Long multiplehostaccess;
    @JsonProperty("total_space")
    private Long totalSpace;
    @JsonProperty("uncommitted")
    private Long uncommitted;
    @JsonProperty("free_space")
    private Long freeSpace;
    @JsonProperty("v_total_vms")
    private Long vTotalVms;
    @JsonProperty("hosts")
    private List<String> hosts = null;
    @JsonProperty("storage_profiles")
    private List<String> storageProfiles = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

}

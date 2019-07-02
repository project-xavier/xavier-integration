
package org.jboss.xavier.integrations.route.model.cloudforms.v1;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "store_type",
    "total_space",
    "free_space",
    "multiplehostaccess",
    "directory_hierarchy_supported",
    "thin_provisioning_supported",
    "raw_disk_mappings_supported"
})
public class Storage {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("store_type")
    private String storeType;
    @JsonProperty("total_space")
    private Integer totalSpace;
    @JsonProperty("free_space")
    private Integer freeSpace;
    @JsonProperty("multiplehostaccess")
    private Integer multiplehostaccess;
    @JsonProperty("directory_hierarchy_supported")
    private Boolean directoryHierarchySupported;
    @JsonProperty("thin_provisioning_supported")
    private Boolean thinProvisioningSupported;
    @JsonProperty("raw_disk_mappings_supported")
    private Boolean rawDiskMappingsSupported;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public Storage withId(Integer id) {
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

    public Storage withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("store_type")
    public String getStoreType() {
        return storeType;
    }

    @JsonProperty("store_type")
    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public Storage withStoreType(String storeType) {
        this.storeType = storeType;
        return this;
    }

    @JsonProperty("total_space")
    public Integer getTotalSpace() {
        return totalSpace;
    }

    @JsonProperty("total_space")
    public void setTotalSpace(Integer totalSpace) {
        this.totalSpace = totalSpace;
    }

    public Storage withTotalSpace(Integer totalSpace) {
        this.totalSpace = totalSpace;
        return this;
    }

    @JsonProperty("free_space")
    public Integer getFreeSpace() {
        return freeSpace;
    }

    @JsonProperty("free_space")
    public void setFreeSpace(Integer freeSpace) {
        this.freeSpace = freeSpace;
    }

    public Storage withFreeSpace(Integer freeSpace) {
        this.freeSpace = freeSpace;
        return this;
    }

    @JsonProperty("multiplehostaccess")
    public Integer getMultiplehostaccess() {
        return multiplehostaccess;
    }

    @JsonProperty("multiplehostaccess")
    public void setMultiplehostaccess(Integer multiplehostaccess) {
        this.multiplehostaccess = multiplehostaccess;
    }

    public Storage withMultiplehostaccess(Integer multiplehostaccess) {
        this.multiplehostaccess = multiplehostaccess;
        return this;
    }

    @JsonProperty("directory_hierarchy_supported")
    public Boolean getDirectoryHierarchySupported() {
        return directoryHierarchySupported;
    }

    @JsonProperty("directory_hierarchy_supported")
    public void setDirectoryHierarchySupported(Boolean directoryHierarchySupported) {
        this.directoryHierarchySupported = directoryHierarchySupported;
    }

    public Storage withDirectoryHierarchySupported(Boolean directoryHierarchySupported) {
        this.directoryHierarchySupported = directoryHierarchySupported;
        return this;
    }

    @JsonProperty("thin_provisioning_supported")
    public Boolean getThinProvisioningSupported() {
        return thinProvisioningSupported;
    }

    @JsonProperty("thin_provisioning_supported")
    public void setThinProvisioningSupported(Boolean thinProvisioningSupported) {
        this.thinProvisioningSupported = thinProvisioningSupported;
    }

    public Storage withThinProvisioningSupported(Boolean thinProvisioningSupported) {
        this.thinProvisioningSupported = thinProvisioningSupported;
        return this;
    }

    @JsonProperty("raw_disk_mappings_supported")
    public Boolean getRawDiskMappingsSupported() {
        return rawDiskMappingsSupported;
    }

    @JsonProperty("raw_disk_mappings_supported")
    public void setRawDiskMappingsSupported(Boolean rawDiskMappingsSupported) {
        this.rawDiskMappingsSupported = rawDiskMappingsSupported;
    }

    public Storage withRawDiskMappingsSupported(Boolean rawDiskMappingsSupported) {
        this.rawDiskMappingsSupported = rawDiskMappingsSupported;
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

    public Storage withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

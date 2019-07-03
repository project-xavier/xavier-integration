
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
    "device_name",
    "device_type",
    "disk_type",
    "free_space",
    "mode",
    "size",
    "size_on_disk",
    "partitions"
})
public class Disk {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("device_name")
    private String deviceName;
    @JsonProperty("device_type")
    private String deviceType;
    @JsonProperty("disk_type")
    private Object diskType;
    @JsonProperty("free_space")
    private Object freeSpace;
    @JsonProperty("mode")
    private Object mode;
    @JsonProperty("size")
    private Object size;
    @JsonProperty("size_on_disk")
    private Object sizeOnDisk;
    @JsonProperty("partitions")
    private List<Object> partitions = null;
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

    public Disk withId(Long id) {
        this.id = id;
        return this;
    }

    @JsonProperty("device_name")
    public String getDeviceName() {
        return deviceName;
    }

    @JsonProperty("device_name")
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Disk withDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    @JsonProperty("device_type")
    public String getDeviceType() {
        return deviceType;
    }

    @JsonProperty("device_type")
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Disk withDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    @JsonProperty("disk_type")
    public Object getDiskType() {
        return diskType;
    }

    @JsonProperty("disk_type")
    public void setDiskType(Object diskType) {
        this.diskType = diskType;
    }

    public Disk withDiskType(Object diskType) {
        this.diskType = diskType;
        return this;
    }

    @JsonProperty("free_space")
    public Object getFreeSpace() {
        return freeSpace;
    }

    @JsonProperty("free_space")
    public void setFreeSpace(Object freeSpace) {
        this.freeSpace = freeSpace;
    }

    public Disk withFreeSpace(Object freeSpace) {
        this.freeSpace = freeSpace;
        return this;
    }

    @JsonProperty("mode")
    public Object getMode() {
        return mode;
    }

    @JsonProperty("mode")
    public void setMode(Object mode) {
        this.mode = mode;
    }

    public Disk withMode(Object mode) {
        this.mode = mode;
        return this;
    }

    @JsonProperty("size")
    public Object getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(Object size) {
        this.size = size;
    }

    public Disk withSize(Object size) {
        this.size = size;
        return this;
    }

    @JsonProperty("size_on_disk")
    public Object getSizeOnDisk() {
        return sizeOnDisk;
    }

    @JsonProperty("size_on_disk")
    public void setSizeOnDisk(Object sizeOnDisk) {
        this.sizeOnDisk = sizeOnDisk;
    }

    public Disk withSizeOnDisk(Object sizeOnDisk) {
        this.sizeOnDisk = sizeOnDisk;
        return this;
    }

    @JsonProperty("partitions")
    public List<Object> getPartitions() {
        return partitions;
    }

    @JsonProperty("partitions")
    public void setPartitions(List<Object> partitions) {
        this.partitions = partitions;
    }

    public Disk withPartitions(List<Object> partitions) {
        this.partitions = partitions;
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

    public Disk withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

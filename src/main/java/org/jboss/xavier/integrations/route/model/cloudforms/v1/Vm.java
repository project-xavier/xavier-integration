
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
    "type",
    "guid",
    "uid_ems",
    "archived",
    "cpu_cores_per_socket",
    "cpu_total_cores",
    "disks_aligned",
    "ems_ref",
    "has_rdm_disk",
    "host_id",
    "linked_clone",
    "orphaned",
    "power_state",
    "ram_size_in_bytes",
    "retired",
    "v_datastore_path",
    "hardware"
})
public class Vm {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String type;
    @JsonProperty("guid")
    private String guid;
    @JsonProperty("uid_ems")
    private String uidEms;
    @JsonProperty("archived")
    private Boolean archived;
    @JsonProperty("cpu_cores_per_socket")
    private Long cpuCoresPerSocket;
    @JsonProperty("cpu_total_cores")
    private Long cpuTotalCores;
    @JsonProperty("disks_aligned")
    private String disksAligned;
    @JsonProperty("ems_ref")
    private String emsRef;
    @JsonProperty("has_rdm_disk")
    private Boolean hasRdmDisk;
    @JsonProperty("host_id")
    private Long hostId;
    @JsonProperty("linked_clone")
    private Boolean linkedClone;
    @JsonProperty("orphaned")
    private Boolean orphaned;
    @JsonProperty("power_state")
    private String powerState;
    @JsonProperty("ram_size_in_bytes")
    private Long ramSizeInBytes;
    @JsonProperty("retired")
    private Object retired;
    @JsonProperty("v_datastore_path")
    private String vDatastorePath;
    @JsonProperty("hardware")
    private Hardware hardware;
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

    public Vm withId(Long id) {
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

    public Vm withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    public Vm withType(String type) {
        this.type = type;
        return this;
    }

    @JsonProperty("guid")
    public String getGuid() {
        return guid;
    }

    @JsonProperty("guid")
    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Vm withGuid(String guid) {
        this.guid = guid;
        return this;
    }

    @JsonProperty("uid_ems")
    public String getUidEms() {
        return uidEms;
    }

    @JsonProperty("uid_ems")
    public void setUidEms(String uidEms) {
        this.uidEms = uidEms;
    }

    public Vm withUidEms(String uidEms) {
        this.uidEms = uidEms;
        return this;
    }

    @JsonProperty("archived")
    public Boolean getArchived() {
        return archived;
    }

    @JsonProperty("archived")
    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Vm withArchived(Boolean archived) {
        this.archived = archived;
        return this;
    }

    @JsonProperty("cpu_cores_per_socket")
    public Long getCpuCoresPerSocket() {
        return cpuCoresPerSocket;
    }

    @JsonProperty("cpu_cores_per_socket")
    public void setCpuCoresPerSocket(Long cpuCoresPerSocket) {
        this.cpuCoresPerSocket = cpuCoresPerSocket;
    }

    public Vm withCpuCoresPerSocket(Long cpuCoresPerSocket) {
        this.cpuCoresPerSocket = cpuCoresPerSocket;
        return this;
    }

    @JsonProperty("cpu_total_cores")
    public Long getCpuTotalCores() {
        return cpuTotalCores;
    }

    @JsonProperty("cpu_total_cores")
    public void setCpuTotalCores(Long cpuTotalCores) {
        this.cpuTotalCores = cpuTotalCores;
    }

    public Vm withCpuTotalCores(Long cpuTotalCores) {
        this.cpuTotalCores = cpuTotalCores;
        return this;
    }

    @JsonProperty("disks_aligned")
    public String getDisksAligned() {
        return disksAligned;
    }

    @JsonProperty("disks_aligned")
    public void setDisksAligned(String disksAligned) {
        this.disksAligned = disksAligned;
    }

    public Vm withDisksAligned(String disksAligned) {
        this.disksAligned = disksAligned;
        return this;
    }

    @JsonProperty("ems_ref")
    public String getEmsRef() {
        return emsRef;
    }

    @JsonProperty("ems_ref")
    public void setEmsRef(String emsRef) {
        this.emsRef = emsRef;
    }

    public Vm withEmsRef(String emsRef) {
        this.emsRef = emsRef;
        return this;
    }

    @JsonProperty("has_rdm_disk")
    public Boolean getHasRdmDisk() {
        return hasRdmDisk;
    }

    @JsonProperty("has_rdm_disk")
    public void setHasRdmDisk(Boolean hasRdmDisk) {
        this.hasRdmDisk = hasRdmDisk;
    }

    public Vm withHasRdmDisk(Boolean hasRdmDisk) {
        this.hasRdmDisk = hasRdmDisk;
        return this;
    }

    @JsonProperty("host_id")
    public Long getHostId() {
        return hostId;
    }

    @JsonProperty("host_id")
    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public Vm withHostId(Long hostId) {
        this.hostId = hostId;
        return this;
    }

    @JsonProperty("linked_clone")
    public Boolean getLinkedClone() {
        return linkedClone;
    }

    @JsonProperty("linked_clone")
    public void setLinkedClone(Boolean linkedClone) {
        this.linkedClone = linkedClone;
    }

    public Vm withLinkedClone(Boolean linkedClone) {
        this.linkedClone = linkedClone;
        return this;
    }

    @JsonProperty("orphaned")
    public Boolean getOrphaned() {
        return orphaned;
    }

    @JsonProperty("orphaned")
    public void setOrphaned(Boolean orphaned) {
        this.orphaned = orphaned;
    }

    public Vm withOrphaned(Boolean orphaned) {
        this.orphaned = orphaned;
        return this;
    }

    @JsonProperty("power_state")
    public String getPowerState() {
        return powerState;
    }

    @JsonProperty("power_state")
    public void setPowerState(String powerState) {
        this.powerState = powerState;
    }

    public Vm withPowerState(String powerState) {
        this.powerState = powerState;
        return this;
    }

    @JsonProperty("ram_size_in_bytes")
    public Long getRamSizeInBytes() {
        return ramSizeInBytes;
    }

    @JsonProperty("ram_size_in_bytes")
    public void setRamSizeInBytes(Long ramSizeInBytes) {
        this.ramSizeInBytes = ramSizeInBytes;
    }

    public Vm withRamSizeInBytes(Long ramSizeInBytes) {
        this.ramSizeInBytes = ramSizeInBytes;
        return this;
    }

    @JsonProperty("retired")
    public Object getRetired() {
        return retired;
    }

    @JsonProperty("retired")
    public void setRetired(Object retired) {
        this.retired = retired;
    }

    public Vm withRetired(Object retired) {
        this.retired = retired;
        return this;
    }

    @JsonProperty("v_datastore_path")
    public String getVDatastorePath() {
        return vDatastorePath;
    }

    @JsonProperty("v_datastore_path")
    public void setVDatastorePath(String vDatastorePath) {
        this.vDatastorePath = vDatastorePath;
    }

    public Vm withVDatastorePath(String vDatastorePath) {
        this.vDatastorePath = vDatastorePath;
        return this;
    }

    @JsonProperty("hardware")
    public Hardware getHardware() {
        return hardware;
    }

    @JsonProperty("hardware")
    public void setHardware(Hardware hardware) {
        this.hardware = hardware;
    }

    public Vm withHardware(Hardware hardware) {
        this.hardware = hardware;
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

    public Vm withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

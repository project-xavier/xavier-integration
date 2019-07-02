
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
    "ems_ref",
    "name",
    "hostname",
    "type",
    "cpu_total_cores",
    "cpu_cores_per_socket",
    "hyperthreading",
    "ram_size",
    "address",
    "ipaddress",
    "power_state",
    "vmm_product",
    "vmm_vendor",
    "vmm_version",
    "total_vcpus",
    "v_owning_cluster",
    "switches",
    "storages",
    "vms"
})
public class Host {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("ems_ref")
    private String emsRef;
    @JsonProperty("name")
    private String name;
    @JsonProperty("hostname")
    private String hostname;
    @JsonProperty("type")
    private String type;
    @JsonProperty("cpu_total_cores")
    private Integer cpuTotalCores;
    @JsonProperty("cpu_cores_per_socket")
    private Integer cpuCoresPerSocket;
    @JsonProperty("hyperthreading")
    private Boolean hyperthreading;
    @JsonProperty("ram_size")
    private Integer ramSize;
    @JsonProperty("address")
    private String address;
    @JsonProperty("ipaddress")
    private String ipaddress;
    @JsonProperty("power_state")
    private String powerState;
    @JsonProperty("vmm_product")
    private String vmmProduct;
    @JsonProperty("vmm_vendor")
    private String vmmVendor;
    @JsonProperty("vmm_version")
    private String vmmVersion;
    @JsonProperty("total_vcpus")
    private Integer totalVcpus;
    @JsonProperty("v_owning_cluster")
    private String vOwningCluster;
    @JsonProperty("switches")
    private List<Switch> switches = null;
    @JsonProperty("storages")
    private List<Storage> storages = null;
    @JsonProperty("vms")
    private List<Vm> vms = null;
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

    public Host withId(Integer id) {
        this.id = id;
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

    public Host withEmsRef(String emsRef) {
        this.emsRef = emsRef;
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

    public Host withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("hostname")
    public String getHostname() {
        return hostname;
    }

    @JsonProperty("hostname")
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Host withHostname(String hostname) {
        this.hostname = hostname;
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

    public Host withType(String type) {
        this.type = type;
        return this;
    }

    @JsonProperty("cpu_total_cores")
    public Integer getCpuTotalCores() {
        return cpuTotalCores;
    }

    @JsonProperty("cpu_total_cores")
    public void setCpuTotalCores(Integer cpuTotalCores) {
        this.cpuTotalCores = cpuTotalCores;
    }

    public Host withCpuTotalCores(Integer cpuTotalCores) {
        this.cpuTotalCores = cpuTotalCores;
        return this;
    }

    @JsonProperty("cpu_cores_per_socket")
    public Integer getCpuCoresPerSocket() {
        return cpuCoresPerSocket;
    }

    @JsonProperty("cpu_cores_per_socket")
    public void setCpuCoresPerSocket(Integer cpuCoresPerSocket) {
        this.cpuCoresPerSocket = cpuCoresPerSocket;
    }

    public Host withCpuCoresPerSocket(Integer cpuCoresPerSocket) {
        this.cpuCoresPerSocket = cpuCoresPerSocket;
        return this;
    }

    @JsonProperty("hyperthreading")
    public Boolean getHyperthreading() {
        return hyperthreading;
    }

    @JsonProperty("hyperthreading")
    public void setHyperthreading(Boolean hyperthreading) {
        this.hyperthreading = hyperthreading;
    }

    public Host withHyperthreading(Boolean hyperthreading) {
        this.hyperthreading = hyperthreading;
        return this;
    }

    @JsonProperty("ram_size")
    public Integer getRamSize() {
        return ramSize;
    }

    @JsonProperty("ram_size")
    public void setRamSize(Integer ramSize) {
        this.ramSize = ramSize;
    }

    public Host withRamSize(Integer ramSize) {
        this.ramSize = ramSize;
        return this;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    public Host withAddress(String address) {
        this.address = address;
        return this;
    }

    @JsonProperty("ipaddress")
    public String getIpaddress() {
        return ipaddress;
    }

    @JsonProperty("ipaddress")
    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public Host withIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
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

    public Host withPowerState(String powerState) {
        this.powerState = powerState;
        return this;
    }

    @JsonProperty("vmm_product")
    public String getVmmProduct() {
        return vmmProduct;
    }

    @JsonProperty("vmm_product")
    public void setVmmProduct(String vmmProduct) {
        this.vmmProduct = vmmProduct;
    }

    public Host withVmmProduct(String vmmProduct) {
        this.vmmProduct = vmmProduct;
        return this;
    }

    @JsonProperty("vmm_vendor")
    public String getVmmVendor() {
        return vmmVendor;
    }

    @JsonProperty("vmm_vendor")
    public void setVmmVendor(String vmmVendor) {
        this.vmmVendor = vmmVendor;
    }

    public Host withVmmVendor(String vmmVendor) {
        this.vmmVendor = vmmVendor;
        return this;
    }

    @JsonProperty("vmm_version")
    public String getVmmVersion() {
        return vmmVersion;
    }

    @JsonProperty("vmm_version")
    public void setVmmVersion(String vmmVersion) {
        this.vmmVersion = vmmVersion;
    }

    public Host withVmmVersion(String vmmVersion) {
        this.vmmVersion = vmmVersion;
        return this;
    }

    @JsonProperty("total_vcpus")
    public Integer getTotalVcpus() {
        return totalVcpus;
    }

    @JsonProperty("total_vcpus")
    public void setTotalVcpus(Integer totalVcpus) {
        this.totalVcpus = totalVcpus;
    }

    public Host withTotalVcpus(Integer totalVcpus) {
        this.totalVcpus = totalVcpus;
        return this;
    }

    @JsonProperty("v_owning_cluster")
    public String getVOwningCluster() {
        return vOwningCluster;
    }

    @JsonProperty("v_owning_cluster")
    public void setVOwningCluster(String vOwningCluster) {
        this.vOwningCluster = vOwningCluster;
    }

    public Host withVOwningCluster(String vOwningCluster) {
        this.vOwningCluster = vOwningCluster;
        return this;
    }

    @JsonProperty("switches")
    public List<Switch> getSwitches() {
        return switches;
    }

    @JsonProperty("switches")
    public void setSwitches(List<Switch> switches) {
        this.switches = switches;
    }

    public Host withSwitches(List<Switch> switches) {
        this.switches = switches;
        return this;
    }

    @JsonProperty("storages")
    public List<Storage> getStorages() {
        return storages;
    }

    @JsonProperty("storages")
    public void setStorages(List<Storage> storages) {
        this.storages = storages;
    }

    public Host withStorages(List<Storage> storages) {
        this.storages = storages;
        return this;
    }

    @JsonProperty("vms")
    public List<Vm> getVms() {
        return vms;
    }

    @JsonProperty("vms")
    public void setVms(List<Vm> vms) {
        this.vms = vms;
    }

    public Host withVms(List<Vm> vms) {
        this.vms = vms;
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

    public Host withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}


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
    "guid",
    "api_version",
    "emstype_description",
    "hostname",
    "ems_clusters"
})
public class ManageIQProvidersVmwareInfraManager {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String type;
    @JsonProperty("guid")
    private String guid;
    @JsonProperty("api_version")
    private String apiVersion;
    @JsonProperty("emstype_description")
    private String emstypeDescription;
    @JsonProperty("hostname")
    private String hostname;
    @JsonProperty("ems_clusters")
    private List<EmsCluster> emsClusters = null;
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

    public ManageIQProvidersVmwareInfraManager withId(Long id) {
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

    public ManageIQProvidersVmwareInfraManager withName(String name) {
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

    public ManageIQProvidersVmwareInfraManager withType(String type) {
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

    public ManageIQProvidersVmwareInfraManager withGuid(String guid) {
        this.guid = guid;
        return this;
    }

    @JsonProperty("api_version")
    public String getApiVersion() {
        return apiVersion;
    }

    @JsonProperty("api_version")
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public ManageIQProvidersVmwareInfraManager withApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    @JsonProperty("emstype_description")
    public String getEmstypeDescription() {
        return emstypeDescription;
    }

    @JsonProperty("emstype_description")
    public void setEmstypeDescription(String emstypeDescription) {
        this.emstypeDescription = emstypeDescription;
    }

    public ManageIQProvidersVmwareInfraManager withEmstypeDescription(String emstypeDescription) {
        this.emstypeDescription = emstypeDescription;
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

    public ManageIQProvidersVmwareInfraManager withHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    @JsonProperty("ems_clusters")
    public List<EmsCluster> getEmsClusters() {
        return emsClusters;
    }

    @JsonProperty("ems_clusters")
    public void setEmsClusters(List<EmsCluster> emsClusters) {
        this.emsClusters = emsClusters;
    }

    public ManageIQProvidersVmwareInfraManager withEmsClusters(List<EmsCluster> emsClusters) {
        this.emsClusters = emsClusters;
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

    public ManageIQProvidersVmwareInfraManager withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

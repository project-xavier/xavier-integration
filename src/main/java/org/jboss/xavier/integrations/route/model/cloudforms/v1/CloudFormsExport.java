
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
    "ManageIQ::Providers::Vmware::InfraManager"
})
public class CloudFormsExport {

    @JsonProperty("ManageIQ::Providers::Vmware::InfraManager")
    private ManageIQProvidersVmwareInfraManager manageIQProvidersVmwareInfraManager;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ManageIQ::Providers::Vmware::InfraManager")
    public ManageIQProvidersVmwareInfraManager getManageIQProvidersVmwareInfraManager() {
        return manageIQProvidersVmwareInfraManager;
    }

    @JsonProperty("ManageIQ::Providers::Vmware::InfraManager")
    public void setManageIQProvidersVmwareInfraManager(ManageIQProvidersVmwareInfraManager manageIQProvidersVmwareInfraManager) {
        this.manageIQProvidersVmwareInfraManager = manageIQProvidersVmwareInfraManager;
    }

    public CloudFormsExport withManageIQProvidersVmwareInfraManager(ManageIQProvidersVmwareInfraManager manageIQProvidersVmwareInfraManager) {
        this.manageIQProvidersVmwareInfraManager = manageIQProvidersVmwareInfraManager;
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

    public CloudFormsExport withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}


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
    "number_of_nics",
    "guest_os_full_name",
    "disks"
})
public class Hardware {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("number_of_nics")
    private Object numberOfNics;
    @JsonProperty("guest_os_full_name")
    private String guestOsFullName;
    @JsonProperty("disks")
    private List<Disk> disks = null;
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

    public Hardware withId(Long id) {
        this.id = id;
        return this;
    }

    @JsonProperty("number_of_nics")
    public Object getNumberOfNics() {
        return numberOfNics;
    }

    @JsonProperty("number_of_nics")
    public void setNumberOfNics(Object numberOfNics) {
        this.numberOfNics = numberOfNics;
    }

    public Hardware withNumberOfNics(Object numberOfNics) {
        this.numberOfNics = numberOfNics;
        return this;
    }

    @JsonProperty("guest_os_full_name")
    public String getGuestOsFullName() {
        return guestOsFullName;
    }

    @JsonProperty("guest_os_full_name")
    public void setGuestOsFullName(String guestOsFullName) {
        this.guestOsFullName = guestOsFullName;
    }

    public Hardware withGuestOsFullName(String guestOsFullName) {
        this.guestOsFullName = guestOsFullName;
        return this;
    }

    @JsonProperty("disks")
    public List<Disk> getDisks() {
        return disks;
    }

    @JsonProperty("disks")
    public void setDisks(List<Disk> disks) {
        this.disks = disks;
    }

    public Hardware withDisks(List<Disk> disks) {
        this.disks = disks;
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

    public Hardware withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

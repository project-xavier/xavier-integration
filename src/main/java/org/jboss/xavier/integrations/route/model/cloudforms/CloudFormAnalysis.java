package org.jboss.xavier.integrations.route.model.cloudforms;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "hostname",
    "emstype_description",
    "api_version",
    "datacenters"
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CloudFormAnalysis {

    @JsonProperty("hostname")
    private String hostname;
    @JsonProperty("emstype_description")
    private String emstypeDescription;
    @JsonProperty("api_version")
    private String apiVersion;
    @JsonProperty("datacenters")
    private List<Datacenter> datacenters = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

}

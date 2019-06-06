
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
    "ems_clusters",
    "datastores"
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Datacenter {

    @JsonProperty("name")
    private String name;
    @JsonProperty("ems_clusters")
    private List<EmsCluster> emsClusters = null;
    @JsonProperty("datastores")
    private List<Datastore> datastores = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();
}

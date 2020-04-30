package org.jboss.xavier.analytics.pojo.output.workload.summary;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class AppIdentifierModel {

    @JsonIgnore
    @EmbeddedId
    private AppIdentifierIDModel id;

    @Column(insertable = false, updatable = false)
    private String groupName;

    @Column(insertable = false, updatable = false)
    private String name;

    private String version;

    private String identifier;

    public AppIdentifierIDModel getId() {
        return id;
    }

    public void setId(AppIdentifierIDModel id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String group) {
        this.groupName = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String vendor) {
        this.name = vendor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}

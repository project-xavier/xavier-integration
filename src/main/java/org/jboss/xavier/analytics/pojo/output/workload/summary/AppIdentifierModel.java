package org.jboss.xavier.analytics.pojo.output.workload.summary;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AppIdentifierModel {

    @Id
    private Long id;

    private String groupName;

    private String name;

    private String version;

    private String identifier;

    private Integer priority;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public static final class Builder {
        private Long id;
        private String groupName;
        private String name;
        private String version;
        private String identifier;
        private Integer priority;

        private Builder() {
        }

        public static Builder anAppIdentifierModel() {
            return new Builder();
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withGroupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder withIdentifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder withPriority(Integer priority) {
            this.priority = priority;
            return this;
        }

        public AppIdentifierModel build() {
            AppIdentifierModel appIdentifierModel = new AppIdentifierModel();
            appIdentifierModel.setId(id);
            appIdentifierModel.setGroupName(groupName);
            appIdentifierModel.setName(name);
            appIdentifierModel.setVersion(version);
            appIdentifierModel.setIdentifier(identifier);
            appIdentifierModel.setPriority(priority);
            return appIdentifierModel;
        }
    }
}

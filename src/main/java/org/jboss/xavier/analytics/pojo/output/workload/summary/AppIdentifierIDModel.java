package org.jboss.xavier.analytics.pojo.output.workload.summary;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AppIdentifierIDModel implements Serializable {

    private String groupName;
    private String name;

    public AppIdentifierIDModel() {
    }

    public AppIdentifierIDModel(String groupName, String name) {
        this.groupName = groupName;
        this.name = name;
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
}

package org.jboss.xavier.integrations.rbac;

import java.util.List;

public class AclData {

    private String operation;
    private List<String> resources;

    public AclData() {
    }

    public AclData(String operation, List<String> resources) {
        this.operation = operation;
        this.resources = resources;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }
}

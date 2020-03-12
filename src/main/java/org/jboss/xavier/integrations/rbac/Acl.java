package org.jboss.xavier.integrations.rbac;

import java.util.List;

/**
 * Access Control List (ACL) define permissions and resource definitions that a user has inside a
 * specific application. It is defined in the field 'data' of {@link RbacResponse}. E.g.:
 *
 * Having:
 * {
 *    "permission":"migration-analytics:myResource:read",
 *    "resourceDefinitions":[
 *       {
 *          "attributeFilter":{
 *             "key":"migration-analytics.rh.account",
 *             "operation":"equal",
 *             "value":"123456"
 *          }
 *       }
 *    ]
 * }
 *
 */
public class Acl {
    private String permission;
    private List<ResourceDefinition> resourceDefinitions;

    public Acl() {
    }

    public Acl(String permission, List<ResourceDefinition> resourceDefinitions) {
        this.permission = permission;
        this.resourceDefinitions = resourceDefinitions;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<ResourceDefinition> getResourceDefinitions() {
        return resourceDefinitions;
    }

    public void setResourceDefinitions(List<ResourceDefinition> resourceDefinitions) {
        this.resourceDefinitions = resourceDefinitions;
    }

    public static class ResourceDefinition {
        private AttributeFilter attributeFilter;

        public ResourceDefinition() {
        }

        public ResourceDefinition(AttributeFilter attributeFilter) {
            this.attributeFilter = attributeFilter;
        }

        public AttributeFilter getAttributeFilter() {
            return attributeFilter;
        }

        public void setAttributeFilter(AttributeFilter attributeFilter) {
            this.attributeFilter = attributeFilter;
        }
    }

    public static class AttributeFilter {
        private String key;
        private String operation;
        private String value;

        public AttributeFilter() {
        }

        public AttributeFilter(String key, String operation, String value) {
            this.key = key;
            this.operation = operation;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

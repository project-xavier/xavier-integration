package org.jboss.xavier.integrations.rbac;

import java.util.List;

/**
 * Utility class for storing a single operation and a list of individual resources assigned to the user.
 * It represents a partial result of processing an ACL and, for instance here {@link RBACUtils#processAcls(List)}.
 *
 * E.g.:
 * Having {@link RbacResponse}:
 * {
 *   "meta": {...},
 *   "links": {...},
 *   "data": [
 *      {
 *          "permission":"migration-analytics:my-application-resource:read",
 *          "resourceDefinitions":[
 *              {
 *                  "attributeFilter": {
 *                      "key":"migration-analytics.rh.account",
 *                      "operation":"in",
 *                      "value":"123456,654321"
 *                  }
 *              }
 *          ]
 *      }
 *   ]
 * }
 *
 * Then we will have this AclData:
 * {
 *    "operation": "read",
 *    "resources":["123456", "654321"]
 * }
 *
 * Which means that the user has read access to the individual resources "123456" and "654321". This result
 * might be interpret as: User has read access (GET) to '/my-application-resource/123456' and '/my-application-resource/654321'
 * endpoints, but he has not access to '/my-application-resource/1111'.
 *
 */
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

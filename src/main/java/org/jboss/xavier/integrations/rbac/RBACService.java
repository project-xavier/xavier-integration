package org.jboss.xavier.integrations.rbac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RBACService {

    public static final String WILDCARD = "*";

    /***
     * Extract resource definition information. Right now it supports just "equal" and "in" operations
     *
     * @return List of resourceDefinition values.
     * E.g. Given:
     * [
     *     {
     *         "attributeFilter": {
     *             "key": "key1",
     *             "operation": "equal",
     *             "value": "1"
     *         }
     *     },
     *     {
     *         "attributeFilter": {
     *             "key": "key2",
     *             "operation": "in",
     *             "value": "2,3,4"
     *         }
     *     }
     * ]
     *
     * Will return ["1", "2", "3", "4", "5"]
     * */
    public static List<String> extractResourceDefinitions(List<Acl.ResourceDefinition> resourceDefinitions) {
        List<String> result = new ArrayList<>();

        if (resourceDefinitions.isEmpty()) {
            return Collections.singletonList(WILDCARD);
        }

        for (Acl.ResourceDefinition resourceDefinition : resourceDefinitions) {
            Acl.AttributeFilter attributeFilter = resourceDefinition.getAttributeFilter();
            String operation = attributeFilter.getOperation();
            String value = attributeFilter.getValue();

            if (operation.equals("equal") && value != null) {
                result.add(value);
            } else if (operation.equals("in") && value != null) {
                result.addAll(Arrays.asList(value.split(",")));
            }
        }

        return result;
    }

    /***
     * Process acls to determine capabilities. Empty resourceDefinitions means all access ('*')
     *
     * @return List of resourceDefinition values.
     * E.g. Given:
     * [
     *     {
     *         "permission": "migration-analytics:resource1:read",
     *         "resourceDefinitions": []
     *     },
     *     {
     *         "permission": "migration-analytics:resource2:write",
     *         "resourceDefinitions": [
     *             {
     *                 "attributeFilter": {
     *                     "key": "key1",
     *                     "operation": "equal",
     *                     "value": "1"
     *                 }
     *             },
     *             {
     *                 "attributeFilter": {
     *                     "key": "key2",
     *                     "operation": "in",
     *                     "value": "2,3,4"
     *                 }
     *             }
     *         ]
     *     }
     * ]
     *
     * Will return:
     * {
     *     "resource1": [
     *         {
     *             "operation": "read",
     *             "resources": [
     *                 "*"
     *             ]
     *         }
     *     ],
     *     "resource2": [
     *         {
     *             "operation": "write",
     *             "resources": [
     *                 "1",
     *                 "2",
     *                 "3",
     *                 "4"
     *             ]
     *         }
     *     ]
     * }
     * */
    public static Map<String, List<AclData>> processAcls(List<Acl> acls) {
        Map<String, List<AclData>> access = new HashMap<>();
        for (Acl acl : acls) {
            String permission = acl.getPermission();
            List<Acl.ResourceDefinition> resourceDefinitions = acl.getResourceDefinitions();

            // extract permission_data
            String[] permComponents = permission.split(":");
            if (permComponents.length != 3) {
                throw new IllegalStateException("Invalid permission definition:" + permission);
            }

            String resourceType = permComponents[1];
            String operation = permComponents[2];

            List<String> resources = extractResourceDefinitions(resourceDefinitions);
            AclData aclData = new AclData(operation, resources);

            access.computeIfAbsent(resourceType, resource -> new ArrayList<>()).add(aclData);
        }

        if (access.isEmpty()) {
            return null;
        }

        return access;
    }


    /***
     * Get operation and default wildcard to write for now.
     **/
    public static String getOperation(AclData accessItem, String resType) {
        String operation = accessItem.getOperation();
        if (operation.equals(WILDCARD)) {
            List<String> operations = ResourceTypes.RESOURCE_TYPES.getOrDefault(resType, Collections.emptyList());
            if (!operations.isEmpty()) {
                operation = operations.get(operations.size() - 1);
            } else {
                throw new IllegalStateException("Invalid resource type: " + resType);
            }
        }

        return operation;
    }

    /***
     * Update access object with access data.
     * @param resourceList list of system defined resources
     **/
    private static Map<String, Map<String, List<String>>> updateAccessObj(
            Map<String, List<AclData>> access,
            Map<String, Map<String, List<String>>> resAccess,
            List<String> resourceList
    ) {
        for (String res : resourceList) {
            List<AclData> accessItems = access.getOrDefault(res, Collections.emptyList());
            for (AclData accessItem : accessItems) {
                String operation = getOperation(accessItem, res);
                List<String> resList = accessItem.getResources();
                if (operation.equals("write") && resAccess.get(res).get("write") != null) {
                    resAccess.get(res).get("write").addAll(resList);
                    resAccess.get(res).get("read").addAll(resList);
                }
                if (operation.equals("read")) {
                    resAccess.get(res).get("read").addAll(resList);
                }
            }
        }

        return resAccess;
    }

    /***
     * Apply access to managed resources.
     * @param access processed Acl
     * @return map of system resources and their operations
     */
    private static Map<String, Map<String, List<String>>> applyAccess(Map<String, List<AclData>> access) {
        Map<String, Map<String, List<String>>> resourceAccess = new HashMap<>();

        List<String> resources = new ArrayList<>();

        for (Map.Entry<String, List<String>> systemResourceType : ResourceTypes.RESOURCE_TYPES.entrySet()) {
            String systemResType = systemResourceType.getKey();
            List<String> systemResOperations = systemResourceType.getValue();

            resources.add(systemResType);
            for (String systemOperation : systemResOperations) {
                if (!resourceAccess.containsKey(systemResType)) {
                    resourceAccess.put(systemResType, new HashMap<>());
                }

                Map<String, List<String>> curr = resourceAccess.get(systemResType);
                curr.put(systemOperation, new ArrayList<>());
            }
        }
        if (access == null) {
            return resourceAccess;
        }

        // process '*' special case
        List<AclData> wildcardItems = access.getOrDefault(WILDCARD, Collections.emptyList());
        for (AclData wildcardItem : wildcardItems) {
            List<String> resList = wildcardItem.getResources();
            for (String resType : resources) {
                String operation = getOperation(wildcardItem, resType);
                AclData acl = new AclData(operation, resList);

                if (!access.containsKey(resType)) {
                    access.put(resType, new ArrayList<>());
                }
                List<AclData> currAccess = access.get(resType);
                currAccess.add(acl);
            }
        }

        resourceAccess = updateAccessObj(access, resourceAccess, resources);

        // compact down to only '*' if present
        for (Map.Entry<String, List<String>> resourceType : ResourceTypes.RESOURCE_TYPES.entrySet()) {
            String resType = resourceType.getKey();
            List<String> operations = resourceType.getValue();

            resources.add(resType);
            for (String operation : operations) {
                Map<String, List<String>> curr = resourceAccess.get(resType);
                List<String> resList = curr.get(operation);
                if (resList.stream().anyMatch(p -> p.equals(WILDCARD))) {
                    curr.put(operation, Collections.singletonList("*"));
                }
            }
        }

        return resourceAccess;
    }

    public static Map<String, Map<String, List<String>>> getAccessForUser(List<Acl> acls) {
        if (acls == null) {
            return null;
        }
        if (acls.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<AclData>> processedAcls = processAcls(acls);
        return applyAccess(processedAcls);
    }
}

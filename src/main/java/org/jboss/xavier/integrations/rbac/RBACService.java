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

    //        """Process acls to determine capabilities."""
    public static Map<String, List<AclData>> processAcls(List<Acl> acls) {
        Map<String, List<AclData>> access = new HashMap<>();
        for (Acl acl : acls) {
            String permission = acl.getPermission();
            List<Acl.ResourceDefinition> resourceDefinitions = acl.getResourceDefinitions();

            // extract permission_data
            String[] permComponents = permission.split(":");
            if (permComponents.length != 3) {
                throw new IllegalStateException("Invalid permission definition permission:" + permission);
            }

            String resourceType = permComponents[1];
            String operation = permComponents[2];

            List<String> resources = extractResourceDefinitions(resourceDefinitions);
            AclData aclData = new AclData(operation, resources);

            if (!access.containsKey(resourceType)) {
                access.put(resourceType, new ArrayList<>());
            }
            access.get(resourceType).add(aclData);
        }

        if (access.isEmpty()) {
            return null;
        }

        return access;
    }

    /**
     * Get operation and default wildcard to write for now.
     */
    private static String get_operation(AclData access_item, String res_type) {
        String operation = access_item.getOperation();
        if (operation.equals(WILDCARD)) {
            List<String> operations = ResourceTypes.RESOURCE_TYPES.getOrDefault(res_type, Collections.emptyList());
            if (!operations.isEmpty()) {
                operation = operations.get(operations.size() - 1);
            } else {
                throw new IllegalStateException("Invalid resource type: " + res_type);
            }
        }

        return operation;
    }

    private static Map<String, Map<String, List<String>>> update_access_obj(Map<String, List<AclData>> access, Map<String, Map<String, List<String>>> res_access, List<String> resource_list) {
//        """Update access object with access data."""
        for (String res : resource_list) {
            List<AclData> access_items = access.getOrDefault(res, Collections.emptyList());
            for (AclData access_item : access_items) {
                String operation = get_operation(access_item, res);
                List<String> res_list = access_item.getResources();
                if (operation.equals("write") && res_access.get(res).get("write") != null) {
                    res_access.get(res).get("write").addAll(res_list);
                    res_access.get(res).get("read").addAll(res_list);
                }
                if (operation.equals("read")) {
                    res_access.get(res).get("read").addAll(res_list);
                }
            }
        }

        return res_access;
    }

    private static Map<String, Map<String, List<String>>> apply_access(Map<String, List<AclData>> access) {
//        """Apply access to managed resources."""
        Map<String, Map<String, List<String>>> res_access = new HashMap<>();
        List<String> resources = new ArrayList<>();

        for (Map.Entry<String, List<String>> resourceType : ResourceTypes.RESOURCE_TYPES.entrySet()) {
            String res_type = resourceType.getKey();
            List<String> operations = resourceType.getValue();

            resources.add(res_type);
            for (String operation : operations) {
                if (!res_access.containsKey(res_type)) {
                    res_access.put(res_type, new HashMap<>());
                }

                Map<String, List<String>> curr = res_access.get(res_type);
                curr.put(operation, new ArrayList<>());
            }
        }
        if (access == null) {
            return res_access;
        }

//    # process '*' special case
        List<AclData> wildcard_items = access.getOrDefault(WILDCARD, Collections.emptyList());
        for (AclData wildcard_item : wildcard_items) {
            List<String> res_list = wildcard_item.getResources();
            for (String res_type : resources) {
                String operation = get_operation(wildcard_item, res_type);
                AclData acl = new AclData(operation, res_list);

                if (!access.containsKey(res_type)) {
                    access.put(res_type, new ArrayList<>());
                }
                List<AclData> curr_access = access.get(res_type);
                curr_access.add(acl);
            }
        }

        res_access = update_access_obj(access, res_access, resources);

//    # compact down to only '*' if present
        for (Map.Entry<String, List<String>> resourceType : ResourceTypes.RESOURCE_TYPES.entrySet()) {
            String res_type = resourceType.getKey();
            List<String> operations = resourceType.getValue();

            resources.add(res_type);
            for (String operation : operations) {
                Map<String, List<String>> curr = res_access.get(res_type);
                List<String> res_list = curr.get(operation);
                if (res_list.stream().anyMatch(p -> p.equals(WILDCARD))) {
                    curr.put(operation, Collections.singletonList("*"));
                }
            }
        }

        return res_access;
    }

    public static Map<String, Map<String, List<String>>> get_access_for_user(List<Acl> acls) {
        if (acls == null) {
            return null;
        }
        if (acls.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<AclData>> processed_acls = processAcls(acls);
        return apply_access(processed_acls);
    }
}

package org.jboss.xavier.integrations.rbac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RBACService {

    public static final String WILDCARD = "*";
    public static final Map<String, List<String>> RESOURCE_TYPES = new HashMap<>();

    static {
        RESOURCE_TYPES.put("user", Collections.singletonList("read"));
        RESOURCE_TYPES.put("analysis", Arrays.asList("read", "write"));
        RESOURCE_TYPES.put("payload", Arrays.asList("read", "write"));
        RESOURCE_TYPES.put("report.initial-saving-estimation", Collections.singletonList("read"));
        RESOURCE_TYPES.put("report.workload-summary", Collections.singletonList("read"));
        RESOURCE_TYPES.put("report.workload-inventory", Collections.singletonList("read"));
        RESOURCE_TYPES.put("mappings", Collections.singletonList("read"));
    }

    /**
     * Get operation and default wildcard to write for now.
     */
    public static String get_operation(AclData access_item, String res_type) {
        String operation = access_item.getOperation();
        if (operation.equals(WILDCARD)) {
            List<String> operations = RESOURCE_TYPES.getOrDefault(res_type, Collections.emptyList());
            if (!operations.isEmpty()) {
                operation = operations.get(operations.size() - 1);
            } else {
                throw new IllegalStateException("Invalid resource type: " + res_type);
            }
        }

        return operation;
    }

    public static Map<String, Map<String, List<String>>> update_access_obj(Map<String, List<AclData>> access, Map<String, Map<String, List<String>>> res_access, List<String> resource_list) {
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

    public static Map<String, Map<String, List<String>>> apply_access(Map<String, List<AclData>> access) {
//        """Apply access to managed resources."""
        Map<String, Map<String, List<String>>> res_access = new HashMap<>();
        List<String> resources = new ArrayList<>();

        for (Map.Entry<String, List<String>> resourceType : RESOURCE_TYPES.entrySet()) {
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
        for (Map.Entry<String, List<String>> resourceType : RESOURCE_TYPES.entrySet()) {
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

    public static List<String> _extract_resource_definitions(List<Acl.ResourceDefinition> resource_definitions) {
//        """Extract resource definition information."""
        List<String> result = new ArrayList<>();
        if (resource_definitions.isEmpty()) {
            return Collections.singletonList(WILDCARD);
        }

        for (Acl.ResourceDefinition res_def : resource_definitions) {
            Acl.AttributeFilter att_filter = res_def.getAttributeFilter();
            String operation = att_filter.getOperation();
            String value = att_filter.getValue();

            if (operation.equals("equal") && value != null) {
                result.add(value);
            }

            if (operation.equals("in") && value != null) {
                result.addAll(Arrays.asList(value.split(",")));
            }
        }

        return result;
    }

    public static Map<String, List<AclData>> process_acls(List<Acl> acls) {
//        """Process acls to determine capabilities."""
        Map<String, List<AclData>> access = new HashMap<>();
        for (Acl acl : acls) {
            String permission = acl.getPermission();
            List<Acl.ResourceDefinition> resource_definitions = acl.getResourceDefinitions();

            // _extract_permission_data
            String[] perm_components = permission.split(":");
            if (perm_components.length != 3) {
                throw new IllegalStateException("Invalid permission definition.");
            }
            String res_typ = perm_components[1];
            String operation = perm_components[2];

            List<String> resources = _extract_resource_definitions(resource_definitions);
            AclData acl_data = new AclData(operation, resources);

            if (!access.containsKey(res_typ)) {
                access.put(res_typ, new ArrayList<>());
            }
            access.get(res_typ).add(acl_data);
        }

        if (access.isEmpty()) {
            return null;
        }

        return access;
    }

    public static Map<String, Map<String, List<String>>> get_access_for_user(List<Acl> acls) {
        if (acls == null || acls.isEmpty()) {
            return null;
        }

        Map<String, List<AclData>> processed_acls = process_acls(acls);
        return apply_access(processed_acls);
    }

    public static void main(String[] args) {
        List<Acl> acls = new ArrayList<>();

        acls.add(
                new Acl("migration-analytics:payload:read", Collections.emptyList())
        );
        acls.add(
                new Acl("migration-analytics:payload:write", Arrays.asList(
                        new Acl.ResourceDefinition(
                                new Acl.AttributeFilter("migration-analytics.payload", "in", "1,3,5")
                        )
                ))
        );
        acls.add(
                new Acl("migration-analytics:payload:write", Arrays.asList(
                        new Acl.ResourceDefinition(
                                new Acl.AttributeFilter("migration-analytics.payload", "equal", "8")
                        )
                ))
        );

        Map<String, Map<String, List<String>>> result = get_access_for_user(acls);
        System.out.println(result);
    }
}

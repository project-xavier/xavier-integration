package org.jboss.xavier.integrations.rbac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RBACUtils {

    public static Map<String, List<String>> processAcls(List<Acl> acls) {
        Map<String, List<String>> access = new HashMap<>();
        for (Acl acl : acls) {
            String permission = acl.getPermission();

            // extract permission_data
            String[] permComponents = permission.split(":");
            if (permComponents.length != 3) {
                throw new IllegalStateException("Invalid permission definition:" + permission);
            }

            String resourceType = permComponents[1];
            String operation = permComponents[2];

            access.computeIfAbsent(resourceType, resource -> new ArrayList<>()).add(operation);
        }

        return access;
    }

    public static Map<String, List<String>> getAccessForUser(List<Acl> acls) {
        if (acls == null) {
            return null;
        }
        if (acls.isEmpty()) {
            return Collections.emptyMap();
        }

        return processAcls(acls);
    }

}

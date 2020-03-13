package org.jboss.xavier.integrations.rbac;

import java.util.List;
import java.util.stream.Collectors;

public class RBACUtils {

    private RBACUtils(){}

    public static List<UserPermission> generateUserPermissions(List<Acl> acls)
    {
        return acls.stream().map(UserPermission::build).collect(Collectors.toList());
    }
}

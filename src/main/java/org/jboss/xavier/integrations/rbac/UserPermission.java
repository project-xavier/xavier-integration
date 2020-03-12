package org.jboss.xavier.integrations.rbac;

import java.util.Objects;

/**
 * Class to keep our internal model for permissions granted to a user
 */
public class UserPermission
{
    private static final String WILDCARD = "*";
    public static final UserPermission WILDCARD_PERMISSION = new UserPermission(WILDCARD, WILDCARD);
    private final String resource;
    private final String action;
    // then add here (in the future) the list of attribute filters: EqualFilter, InFilter and so on

    public UserPermission(String resource, String action)
    {
        this.resource = resource;
        this.action = action;
    }

    public static UserPermission build(Acl acl)
    {
        Objects.requireNonNull(acl, "Null Acl");
        String permission = Objects.requireNonNull(acl.getPermission(), "Null permission definition");
        String[] permissionComponents = permission.split(":");
        if (permissionComponents.length != 3) {
            throw new IllegalStateException("Invalid permission definition:" + permission);
        }
        else
        {
            return new UserPermission(permissionComponents[1], permissionComponents[2]);
        }
    }

    public static UserPermission buildWildcardResource(String action)
    {
        Objects.requireNonNull(action, "Null action definition");
        return new UserPermission(WILDCARD, action);
    }

    public static UserPermission buildWildcardAction(String resource)
    {
        Objects.requireNonNull(resource, "Null resource definition");
        return new UserPermission(resource, WILDCARD);
    }

/*
    Option 1
    public String getResource()
    {
        return resource;
    }

    public String getAction()
    {
        return action;
    }
*/

    public boolean equalsWildcardPermissions(UserPermission userPermission)
    {
        return userPermission.equals(UserPermission.WILDCARD_PERMISSION) ||
                userPermission.equals(UserPermission.buildWildcardAction(userPermission.resource)) ||
                userPermission.equals(UserPermission.buildWildcardResource(userPermission.action));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPermission that = (UserPermission) o;
        return resource.equals(that.resource) &&
                action.equals(that.action);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(resource, action);
    }
}

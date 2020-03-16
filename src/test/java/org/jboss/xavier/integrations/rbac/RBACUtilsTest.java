package org.jboss.xavier.integrations.rbac;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RBACUtilsTest {

    @Test
    public void getAccessForUser_givenAclDefinitions_shouldReturnListOfUserPermissions() {
        //Given
        List<Acl> acls = Arrays.asList(
                new Acl("application-name:resource1:read", Collections.emptyList()),
                new Acl("application-name:resource1:write", Collections.emptyList()),
                new Acl("application-name:resource2:read", Collections.emptyList()),
                new Acl("application-name:resource2:delete", Collections.emptyList())
        );

        //When
        List<UserPermission> userPermissions = RBACUtils.generateUserPermissions(acls);

        //Then
        assertThat(userPermissions).isNotNull();
        assertThat(userPermissions).hasSize(4);

        assertThat(userPermissions).containsOnlyOnce(new UserPermission("resource1", "read"));
        assertThat(userPermissions).containsOnlyOnce(new UserPermission("resource1", "write"));

        assertThat(userPermissions).containsOnlyOnce(new UserPermission("resource2", "read"));
        assertThat(userPermissions).containsOnlyOnce(new UserPermission("resource2", "delete"));
    }


}

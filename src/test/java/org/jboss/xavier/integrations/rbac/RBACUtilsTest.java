package org.jboss.xavier.integrations.rbac;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RBACUtilsTest {

    @Test
    public void getAccessForUser_givenAclDefinitions_shouldReturnMapOfResourcesAndOperations() {
        //Given
        List<Acl> acls = Arrays.asList(
                new Acl("application-name:resource1:read", Collections.emptyList()),
                new Acl("application-name:resource1:write", Collections.emptyList()),
                new Acl("application-name:resource2:read", Collections.emptyList()),
                new Acl("application-name:resource2:delete", Collections.emptyList())
        );

        //When
        Map<String, List<String>> access = RBACUtils.getAccessForUser(acls);

        //Then
        assertThat(access).isNotNull();
        assertThat(access.size()).isEqualTo(2);

        assertThat(access.get("resource1")).hasSize(2);
        assertThat(access.get("resource1")).containsAll(Arrays.asList("read", "write"));

        assertThat(access.get("resource2")).hasSize(2);
        assertThat(access.get("resource2")).containsAll(Arrays.asList("read", "delete"));
    }


}

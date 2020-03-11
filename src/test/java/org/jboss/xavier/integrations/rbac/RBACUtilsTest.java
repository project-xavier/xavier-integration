package org.jboss.xavier.integrations.rbac;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RBACUtilsTest {

    @Test
    public void extractResourceDefinitions_givenEmptyResourceDefinitions_shouldReturnWildcard() {
        //Given
        List<Acl.ResourceDefinition> resourceDefinitions = Collections.emptyList();

        //When
        List<String> result = RBACUtils.extractResourceDefinitions(resourceDefinitions);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(RBACUtils.WILDCARD);
    }

    @Test
    public void extractResourceDefinitions_givenResourceDefinitions_shouldReturnCollection() {
        //Given
        List<Acl.ResourceDefinition> resourceDefinitions = Arrays.asList(
                new Acl.ResourceDefinition(
                        new Acl.AttributeFilter("key1", "equal", "1")
                ),
                new Acl.ResourceDefinition(
                        new Acl.AttributeFilter("key1", "in", "2,3,4")
                ),
                new Acl.ResourceDefinition(
                        new Acl.AttributeFilter("key1", "anotherOperation", "5")
                )
        );

        //When
        List<String> result = RBACUtils.extractResourceDefinitions(resourceDefinitions);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(4);
        assertThat(result).containsAll(Arrays.asList("1", "2", "3", "4"));
    }


    //


    @Test
    public void processAcls_givenMultipleAcl_shouldReturnProcessedAcl() {
        //Given
        List<Acl> acls = Arrays.asList(
                new Acl(
                        "application:resource1:operation1",
                        Collections.emptyList()
                ),
                new Acl(
                        "application:resource2:operation2",
                        Collections.emptyList()
                )
        );

        //When
        Map<String, List<AclData>> processedAcls = RBACUtils.processAcls(acls);

        //Then
        assertThat(processedAcls).isNotNull();
        assertThat(processedAcls).hasSize(2);

        List<AclData> resource1 = processedAcls.get("resource1");
        assertThat(resource1).isNotNull();
        assertThat(resource1).hasSize(1);
        assertThat(resource1.get(0).getOperation()).isEqualTo("operation1");
        assertThat(resource1.get(0).getResources()).containsAll(Collections.singletonList(RBACUtils.WILDCARD));

        List<AclData> resource2 = processedAcls.get("resource2");
        assertThat(resource2).isNotNull();
        assertThat(resource2).hasSize(1);
        assertThat(resource2.get(0).getOperation()).isEqualTo("operation2");
        assertThat(resource2.get(0).getResources()).containsAll(Collections.singletonList(RBACUtils.WILDCARD));
    }

    @Test
    public void processAcls_givenCommonResourceName_shouldMergeAclResult() {
        //Given
        List<Acl> acls = Arrays.asList(
                new Acl(
                        "application:resource1:operation1",
                        Collections.emptyList()
                ),
                new Acl(
                        "application:resource1:operation2",
                        Collections.emptyList()
                )
        );

        //When
        Map<String, List<AclData>> processedAcls = RBACUtils.processAcls(acls);

        //Then
        assertThat(processedAcls).isNotNull();
        assertThat(processedAcls).hasSize(1);

        List<AclData> resource1 = processedAcls.get("resource1");
        assertThat(resource1).isNotNull();
        assertThat(resource1).hasSize(2);
        assertThat(resource1.get(0).getOperation()).isEqualTo("operation1");
        assertThat(resource1.get(0).getResources()).containsAll(Collections.singletonList(RBACUtils.WILDCARD));
        assertThat(resource1.get(1).getOperation()).isEqualTo("operation2");
        assertThat(resource1.get(1).getResources()).containsAll(Collections.singletonList(RBACUtils.WILDCARD));
    }

    @Test
    public void processAcls_givenMultipleAclAndResourceDefinitions_shouldReturnProcessedAcl() {
        //Given
        List<Acl> acls = Arrays.asList(
                new Acl(
                        "application:resource1:operation1",
                        Collections.emptyList()
                ),
                new Acl(
                        "application:resource2:operation2",
                        Arrays.asList(
                                new Acl.ResourceDefinition(
                                        new Acl.AttributeFilter("key1", "equal", "1")
                                ),
                                new Acl.ResourceDefinition(
                                        new Acl.AttributeFilter("key2", "in", "2,3,4")
                                )
                        )
                )
        );

        //When
        Map<String, List<AclData>> processedAcls = RBACUtils.processAcls(acls);

        //Then
        assertThat(processedAcls).isNotNull();
        assertThat(processedAcls).hasSize(2);

        List<AclData> resource1 = processedAcls.get("resource1");
        assertThat(resource1).isNotNull();
        assertThat(resource1).hasSize(1);
        assertThat(resource1.get(0).getOperation()).isEqualTo("operation1");
        assertThat(resource1.get(0).getResources()).containsAll(Collections.singletonList(RBACUtils.WILDCARD));

        List<AclData> resource2 = processedAcls.get("resource2");
        assertThat(resource2).isNotNull();
        assertThat(resource2).hasSize(1);
        assertThat(resource2.get(0).getOperation()).isEqualTo("operation2");
        assertThat(resource2.get(0).getResources()).containsAll(Arrays.asList("1", "2", "3", "4"));
    }


    //


    @Test
    public void getOperation_givenNonAsterixOperation_shouldReturnOriginalOperation() {
        //Given
        AclData aclData = new AclData("read", Collections.emptyList());
        String resource = "resource1";

        //When
        String resultOperation = RBACUtils.getOperation(aclData, resource);

        //Then
        assertThat(resultOperation).isNotNull();
        assertThat(resultOperation).isEqualTo("read");
    }


    //


    @Test
    public void getAccessForUser_givenNull_shouldReturnNull() {
        Map<String, Map<String, List<String>>> access_for_user = RBACUtils.getAccessForUser(null);
        assertThat(access_for_user).isNull();
    }

    @Test
    public void getAccessForUser_givenEmpty_shouldReturnEmpty() {
        Map<String, Map<String, List<String>>> access_for_user = RBACUtils.getAccessForUser(new ArrayList<>());
        assertThat(access_for_user).isNotNull();
        assertThat(access_for_user).isEmpty();
    }

    @Test
    public void getAccessForUser_givenAsterixAndAsterix_shouldReturnAll() {
        List<Acl> acls = Collections.singletonList(
                new Acl("applicationName:*:*", new ArrayList<>())
        );

        Map<String, Map<String, List<String>>> access_for_user = RBACUtils.getAccessForUser(acls);
        assertThat(access_for_user).isNotNull();
        assertThat(access_for_user.size()).isEqualTo(ResourceTypes.RESOURCE_TYPES.size());

        for (Map.Entry<String, List<String>> systemResourcesEntry : ResourceTypes.RESOURCE_TYPES.entrySet()) {
            Map<String, List<String>> userRbacResourcesAccess = access_for_user.get(systemResourcesEntry.getKey());
            assertThat(userRbacResourcesAccess).isNotNull();

            for (String systemOperation : systemResourcesEntry.getValue()) {
                assertThat(userRbacResourcesAccess.containsKey(systemOperation)).isTrue();

                List<String> userRbacAllowedResources = userRbacResourcesAccess.get(systemOperation);

                // Verify
                assertThat(userRbacAllowedResources).isNotNull();
                assertThat(userRbacAllowedResources.size()).isEqualTo(1);
                assertThat(userRbacAllowedResources.get(0)).isEqualTo(RBACUtils.WILDCARD);
            }
        }
    }

    @Test
    public void getAccessForUser_givenResourceNameAndAsterixOperations_shouldReturnAllOperationsForResource() {
        List<Acl> acls = Collections.singletonList(
                new Acl("applicationName:*:*", new ArrayList<>())
        );

        Map<String, Map<String, List<String>>> access_for_user = RBACUtils.getAccessForUser(acls);
        assertThat(access_for_user).isNotNull();
        assertThat(access_for_user.size()).isEqualTo(ResourceTypes.RESOURCE_TYPES.size());

        for (Map.Entry<String, List<String>> systemResourcesEntry : ResourceTypes.RESOURCE_TYPES.entrySet()) {
            Map<String, List<String>> userRbacResourcesAccess = access_for_user.get(systemResourcesEntry.getKey());
            assertThat(userRbacResourcesAccess).isNotNull();

            for (String systemOperation : systemResourcesEntry.getValue()) {
                assertThat(userRbacResourcesAccess.containsKey(systemOperation)).isTrue();

                List<String> userRbacAllowedResources = userRbacResourcesAccess.get(systemOperation);

                // Verify
                assertThat(userRbacAllowedResources).isNotNull();
                assertThat(userRbacAllowedResources.size()).isEqualTo(1);
                assertThat(userRbacAllowedResources.get(0)).isEqualTo(RBACUtils.WILDCARD);
            }
        }
    }
}

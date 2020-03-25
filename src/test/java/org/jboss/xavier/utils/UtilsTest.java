package org.jboss.xavier.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class UtilsTest {

    @Test
    public void utilTest_getTextValueFieldValueFromJsonNode_shouldGiveNode() throws IOException {
        // Given
        String json = "{\"entitlements\":{\"insights\":{\"is_entitled\":true},\"openshift\":{\"is_entitled\":true},\"smart_management\":{\"is_entitled\":false},\"hybrid_cloud\":{\"is_entitled\":true}},\"identity\":{\"internal\":{\"auth_time\":0,\"auth_type\":\"jwt-auth\",\"org_id\":\"6340056\"},\"account_number\":\"1460290\",\"user\":{\"first_name\":\"Marco\",\"is_active\":true,\"is_internal\":true,\"last_name\":\"Rizzi\",\"locale\":\"en_US\",\"is_org_admin\":false,\"username\":\"mrizzi@redhat.com\",\"email\":\"mrizzi+qa@redhat.com\"},\"type\":\"User\"}}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        // When
        Optional<JsonNode> fieldNode = Utils.getFieldValueFromJsonNode(jsonNode, "entitlements", "insights", "is_entitled");

        // Then
        assertThat(fieldNode.isPresent()).isTrue();
        assertThat(fieldNode.map(JsonNode::booleanValue).get()).isTrue();
    }

    @Test
    public void utilTest_getTextValueFieldValueFromJsonNode_givenNoExistingNode_shouldReturnNull() throws IOException {
        // Given
        String json = "{\"entitlements\":{\"insights\":{\"is_entitled\":true},\"openshift\":{\"is_entitled\":true},\"smart_management\":{\"is_entitled\":false},\"hybrid_cloud\":{\"is_entitled\":true}},\"identity\":{\"internal\":{\"auth_time\":0,\"auth_type\":\"jwt-auth\",\"org_id\":\"6340056\"},\"account_number\":\"1460290\",\"user\":{\"first_name\":\"Marco\",\"is_active\":true,\"is_internal\":true,\"last_name\":\"Rizzi\",\"locale\":\"en_US\",\"is_org_admin\":false,\"username\":\"mrizzi@redhat.com\",\"email\":\"mrizzi+qa@redhat.com\"},\"type\":\"User\"}}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        // When
        Optional<JsonNode> fieldNode = Utils.getFieldValueFromJsonNode(jsonNode, "entitlements", "no_existing_node", "is_entitled");

        // Then
        assertThat(fieldNode.isPresent()).isFalse();
    }
}

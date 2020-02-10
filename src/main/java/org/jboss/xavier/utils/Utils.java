package org.jboss.xavier.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public class Utils {

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static JsonNode getFieldValueFromJsonNode(JsonNode node, String ...fieldName) {
        for (String s : fieldName) {
            if (node != null) {
                node = node.get(s);
            }
        }

        return node;
    }

}

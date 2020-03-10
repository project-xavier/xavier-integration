package org.jboss.xavier.integrations.rbac;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for configuring a map of all the resources of the application and its corresponding
 * available operations. E.g.:
 * {
 *    "resource1":[
 *       "operation1"
 *    ],
 *    "resource2":[
 *       "operation1",
 *       "operation2"
 *    ]
 * }
 */
public class ResourceTypes {

    public static final Map<String, List<String>> RESOURCE_TYPES = new HashMap<>();

    static {
        RESOURCE_TYPES.put("user", Collections.singletonList("read"));
    }

}

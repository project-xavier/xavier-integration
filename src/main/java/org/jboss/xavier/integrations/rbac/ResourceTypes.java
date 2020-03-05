package org.jboss.xavier.integrations.rbac;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceTypes {

    public static final Map<String, List<String>> RESOURCE_TYPES = new HashMap<>();

    static {
        RESOURCE_TYPES.put("user", Collections.singletonList("read"));
    }

}

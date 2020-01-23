package org.jboss.xavier.integrations.rbac;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceTypes {

    public static final Map<String, List<String>> RESOURCE_TYPES = new HashMap<>();

    static {
        RESOURCE_TYPES.put("user", Collections.singletonList("read"));
        RESOURCE_TYPES.put("analysis", Arrays.asList("read", "write"));
        RESOURCE_TYPES.put("payload", Arrays.asList("read", "write"));
        RESOURCE_TYPES.put("report.initial-saving-estimation", Collections.singletonList("read"));
        RESOURCE_TYPES.put("report.workload-summary", Collections.singletonList("read"));
        RESOURCE_TYPES.put("report.workload-inventory", Collections.singletonList("read"));
        RESOURCE_TYPES.put("mappings", Collections.singletonList("read"));
    }

}

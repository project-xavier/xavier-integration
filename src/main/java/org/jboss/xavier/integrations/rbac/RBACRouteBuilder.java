package org.jboss.xavier.integrations.rbac;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class RBACRouteBuilder extends RouteBuilder {

    private static final String RBAC_TMP_BODY = "rbacTmpBody";
    public static final String RBAC_USER_ACCESS_HEADER_NAME = "rbacUserAccess";
    public static final String RBAC_ENDPOINT_RESOURCE_NAME = "rbacEndpointResourceName";
    public static final String RBAC_ENDPOINT_RESOURCE_PERMISSION = "rbacEndpointResourcePermission";

    @Value("${insights.rbac.host}")
    private String rbacHost;

    @Value("${insights.rbac.applicationName}")
    private String rbacApplicationName;

    public void configure() throws Exception {
        from("direct:fetch-and-process-rbac-user-access")
                .routeId("fetch-and-process-rbac-user-access")
                .setHeader(RBAC_TMP_BODY, body())
//                .setHeader(Exchange.HTTP_QUERY, constant("application=" + rbacApplicationName))
//                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
                .to("http4://" + rbacHost + "/api/rbac/v1/access?bridgeEndpoint=true").id("rbac-server-access-endpoint")
                .bean(RBACService.class, "get_access_for_user")
                .setHeader(RBAC_USER_ACCESS_HEADER_NAME, body())
                .setBody(exchange -> exchange.getIn().getHeader(RBAC_TMP_BODY));

        from("direct:check-rbac-permissions")
                .routeId("check-rbac-permissions")
                .choice()
                    .when(exchange -> {
                        String endpointResourceName = (String) exchange.getIn().getHeader(RBAC_ENDPOINT_RESOURCE_NAME);
                        String endpointResourcePermission = (String) exchange.getIn().getHeader(RBAC_ENDPOINT_RESOURCE_PERMISSION);

                        @SuppressWarnings("unchecked")
                        Map<String, Map<String, List<String>>> acl = (Map<String, Map<String, List<String>>>) exchange.getIn().getHeader(RBAC_USER_ACCESS_HEADER_NAME);

                        Map<String, List<String>> resourceAcl = acl.getOrDefault(endpointResourceName, Collections.emptyMap());
                        List<String> permissionAcl = resourceAcl.getOrDefault(endpointResourcePermission, Collections.emptyList());
                        return permissionAcl.isEmpty();
                    })
                    .to("direct:request-forbidden")
                .endChoice();
    }

}

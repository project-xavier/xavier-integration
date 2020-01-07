package org.jboss.xavier.integrations.rbac;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.jboss.xavier.integrations.route.model.PageBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class RBACRouteBuilder extends RouteBuilder {

    @Value("${insights.rbac.host}")
    private String rbacHost;

    @Value("${insights.rbac.applicationName}")
    private String rbacApplicationName;

    public void configure() throws Exception {
        from("direct:check-allowed-request")
                .routeId("check-allowed-request")
                .log("Starting RBAC Router logger")
                .setHeader("tmpBody", body())
//                .setHeader(Exchange.HTTP_QUERY, constant("application=" + rbacApplicationName))
//                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
                .to("http4://" + rbacHost + "/api/rbac/v1/access?bridgeEndpoint=true").id("rbac-server-rest")
                .bean(RBACService.class, "get_access_for_user")
                .setHeader("rbacAcl", body())
                .setBody(exchange -> exchange.getIn().getHeader("tmpBody"));

        from("direct:check-rbac-permissions")
                .routeId("check-rbac-permissions")
                .choice()
                    .when(exchange -> {
                        String resource = (String) exchange.getIn().getHeader("rbacResource");
                        String permission = (String) exchange.getIn().getHeader("rbacPermission");
                        Map<String, Map<String, List<String>>> acl = (Map<String, Map<String, List<String>>>) exchange.getIn().getHeader("rbacAcl");

                        Map<String, List<String>> resourceAcl = acl.getOrDefault(resource, Collections.emptyMap());
                        List<String> permissionAcl = resourceAcl.getOrDefault(permission, Collections.emptyList());
                        return permissionAcl.isEmpty();
                    })
                    .to("direct:request-forbidden")
                .endChoice();
    }

}

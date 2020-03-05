package org.jboss.xavier.integrations.rbac;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class RBACRouteBuilder extends RouteBuilder {

    private static final String RBAC_X_RH_IDENTITY = "x-rh-identity";
    private static final String RBAC_X_RH_IDENTITY_DECODED = "rbacXRhIdentityDecoded";
    private static final String RBAC_IS_ORG_ADMIN = "rbacIsOrgAdmin";
    public static final String RBAC_USER_ACCESS = "rbacUserAccess";

    public static final String RBAC_ENDPOINT_RESOURCE_NAME = "rbacEndpointResourceName";
    public static final String RBAC_ENDPOINT_RESOURCE_PERMISSION = "rbacEndpointResourcePermission";

    @Value("${insights.rbac.host}")
    private String rbacHost;

    @Value("${insights.rbac.path}")
    private String rbacPath;

    @Value("${insights.rbac.applicationName}")
    private String rbacApplicationName;

    public void configure() throws Exception {
        from("direct:fetch-and-process-rbac-user-access")
                .routeId("fetch-and-process-rbac-user-access")

                .choice()
                    .when(exchange -> exchange.getIn().getHeader(RBAC_X_RH_IDENTITY) == null)
                        .to("direct:request-forbidden")
                .end()

                .process(exchange -> {
                    // save decoded x-rh-identity JsonNode as header
                    String xRHIdentity = exchange.getIn().getHeader(RBAC_X_RH_IDENTITY, String.class);
                    JsonNode xRHIdentityDecodedJsonNode;
                    try {
                        xRHIdentityDecodedJsonNode = new ObjectMapper().reader().readTree(
                                new String(
                                        Base64.getDecoder().decode(xRHIdentity)
                                )
                        );
                    } catch (IOException e) {
                        Logger.getLogger(this.getClass().getName()).warning("Unable to read " + RBAC_X_RH_IDENTITY);
                        throw new IllegalStateException("Could not read header " + RBAC_X_RH_IDENTITY);
                    }
                    exchange.getIn().setHeader(RBAC_X_RH_IDENTITY_DECODED, xRHIdentityDecodedJsonNode);
                })
                .process(exchange -> {
                    // isOrgAdmin
                    JsonNode xRHIdentity = exchange.getIn().getHeader(RBAC_X_RH_IDENTITY_DECODED, JsonNode.class);
                    JsonNode isOrgAdmin = xRHIdentity.get("identity").get("user").get("is_org_admin");
                    exchange.getIn().setHeader(RBAC_IS_ORG_ADMIN, isOrgAdmin.booleanValue());
                })
                .choice()
                    .when(exchange -> exchange.getIn().getHeader(RBAC_IS_ORG_ADMIN, Boolean.class))
                        .setHeader(RBAC_USER_ACCESS, constant(null))
                    .endChoice()
                    .otherwise()
                        .enrich("direct:fetch-rbac-user-access", (oldExchange, newExchange) -> {
                            List<Acl> acls = newExchange.getIn().getBody(List.class);
                            Map<String, Map<String, List<String>>> accessForUser = RBACService.getAccessForUser(acls);

                            oldExchange.getIn().setHeader(RBAC_USER_ACCESS, accessForUser);
                            return oldExchange;
                        })
                    .endChoice()
                .end();

        from("direct:fetch-rbac-user-access")
                .routeId("fetch-rbac-user-access")
                .setHeader("access", ArrayList::new)
                .setHeader("nextLink", () -> "")
                .loopDoWhile(exchange -> exchange.getIn().getHeader("nextLink") != null)
                    .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
                    .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                    .setHeader(Exchange.HTTP_PATH, constant(rbacPath))
                    .process(exchange -> {
                        String httpQuery;
                        String nextLink = exchange.getIn().getHeader("nextLink", String.class);
                        int queryParamsIndex = nextLink.indexOf("?");
                        if (queryParamsIndex != -1) {
                            httpQuery = nextLink.substring(queryParamsIndex + 1);
                        } else {
                            httpQuery = "application=" + rbacApplicationName + "&limit=100&offset=0";
                        }
                        exchange.getIn().setHeader(Exchange.HTTP_QUERY, httpQuery);
                    })
                    .setHeader(Exchange.HTTP_URI, simple(rbacHost))
                    .setBody(() -> null)
                    .to("http4://oldhost").id("fetch-rbac-user-access-endpoint")
                    .choice()
                        .when(exchange -> exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class) != 200)
                            .log("Invalid user access response responseCode:${header.CamelHttpResponseCode} responseText:${header.CamelHttpResponseText}")
                            .setHeader("nextLink", () -> null)
                        .otherwise()
                            .convertBodyTo(String.class)
                            .process(exchange -> {
                                String body = exchange.getIn().getBody(String.class);
                                RbacResponse rbacResponse = new ObjectMapper().readValue(body, RbacResponse.class);
                                List<Acl> access = rbacResponse.getData();
                                exchange.getIn().getHeader("access", List.class).addAll(access);

                                // Pagination
                                RbacResponse.Links links = rbacResponse.getLinks();
                                exchange.getIn().setHeader("nextLink", links.getNext());
                            })
                    .end()
                .end()
                .setBody(exchange -> exchange.getIn().getHeader("access"));

        from("direct:check-rbac-permissions")
                .routeId("check-rbac-permissions")
                .choice()
                    .when(exchange -> {
                        @SuppressWarnings("unchecked")
                        Map<String, Map<String, List<String>>> acl = (Map<String, Map<String, List<String>>>) exchange.getIn().getHeader(RBAC_USER_ACCESS);
                        // Null means access to everything
                        if (acl == null) {
                            return false;
                        }

                        String endpointResourceName = (String) exchange.getIn().getHeader(RBAC_ENDPOINT_RESOURCE_NAME);
                        String endpointResourcePermission = (String) exchange.getIn().getHeader(RBAC_ENDPOINT_RESOURCE_PERMISSION);

                        Map<String, List<String>> resourceAcl = acl.getOrDefault(endpointResourceName, Collections.emptyMap());
                        List<String> permissionAcl = resourceAcl.getOrDefault(endpointResourcePermission, Collections.emptyList());
                        return permissionAcl.isEmpty();
                    })
                    .to("direct:request-forbidden")
                .endChoice();
    }

}

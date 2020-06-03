package org.jboss.xavier.integrations.rbac;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.jboss.xavier.integrations.route.RouteBuilderExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

import javax.inject.Inject;

@Component
public class RBACRouteBuilder extends RouteBuilder {

    public static final String RBAC_ENDPOINT_RESOURCE_NAME = "rbacEndpointResourceName";
    public static final String RBAC_ENDPOINT_RESOURCE_PERMISSION = "rbacEndpointResourcePermission";

    protected static final String RBAC_NEXT_LINK = "rbacNextLink";
    private static final String RBAC_USER_ACCESS = "rbacUserAccess";
    protected static final String RBAC_USER_PERMISSIONS = "rbacUserPermissions";

    @Value("${insights.rbac.host}")
    private String rbacHost;

    @Value("${insights.rbac.path}")
    private String rbacPath;

    @Value("${insights.rbac.applicationName}")
    private String rbacApplicationName;

    private final BiPredicate<UserPermission, UserPermission> isRequestAllowed = (userPermission, requiredPermission) ->
            userPermission.equalsWildcardPermissions(requiredPermission) ||
            userPermission.equals(requiredPermission);

    @Inject
    private ObjectMapper objectMapper;

    public void configure() throws Exception {
        from("direct:fetch-and-process-rbac-user-access")
                .routeId("fetch-and-process-rbac-user-access")

                .choice()
                    .when(exchange -> exchange.getIn().getHeader(RouteBuilderExceptionHandler.X_RH_IDENTITY) == null)
                        .to("direct:request-forbidden")
                .end()

                .enrich("direct:fetch-rbac-user-access", (oldExchange, newExchange) -> {
                    List<Acl> acls = newExchange.getIn().getBody(List.class);
                    List<UserPermission> userPermissions = RBACUtils.generateUserPermissions(acls);
                    oldExchange.getIn().setHeader(RBAC_USER_PERMISSIONS, userPermissions);
                    return oldExchange;
                });

        from("direct:fetch-rbac-user-access")
                .routeId("fetch-rbac-user-access")
                .setHeader(RBAC_USER_ACCESS, ArrayList::new)
                .setHeader(RBAC_NEXT_LINK, constant(""))
                .loopDoWhile(exchange -> exchange.getIn().getHeader(RBAC_NEXT_LINK) != null)
                    .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
                    .setHeader(Exchange.CONTENT_TYPE, constant(ContentType.APPLICATION_JSON.getMimeType())) // Content-type is a way to specify the media type of request being sent from the client to the server (valid on POST and PUT)
                    .setHeader(HttpHeaders.ACCEPT, constant(ContentType.APPLICATION_JSON.getMimeType())) // Accept header is a way for a client to specify the media type of the response content it is expecting.
                    .setHeader(Exchange.HTTP_PATH, constant(rbacPath))
                    .process(exchange -> {
                        String httpQuery;
                        String nextLink = exchange.getIn().getHeader(RBAC_NEXT_LINK, String.class);
                        int queryParamsIndex = nextLink.indexOf('?');
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
                            .setHeader(RBAC_NEXT_LINK, () -> null)
                        .otherwise()
                            .convertBodyTo(String.class)
                            .process(exchange -> {
                                String body = exchange.getIn().getBody(String.class);
                                RbacResponse rbacResponse = objectMapper.readValue(body, RbacResponse.class);
                                List<Acl> access = rbacResponse.getData();
                                exchange.getIn().getHeader(RBAC_USER_ACCESS, List.class).addAll(access);

                                // Pagination
                                RbacResponse.Links links = rbacResponse.getLinks();
                                exchange.getIn().setHeader(RBAC_NEXT_LINK, links.getNext());
                            })
                    .end()
                .end()
                .setBody(exchange -> exchange.getIn().getHeader(RBAC_USER_ACCESS));

        from("direct:check-rbac-permissions")
                .routeId("check-rbac-permissions")
                .choice()
                    .when(exchange -> {
                        @SuppressWarnings("unchecked")
                        List<UserPermission> userPermissions = exchange.getIn().getHeader(RBAC_USER_PERMISSIONS, List.class);
                        String endpointResourceName = exchange.getIn().getHeader(RBAC_ENDPOINT_RESOURCE_NAME, String.class);
                        String endpointResourcePermission = exchange.getIn().getHeader(RBAC_ENDPOINT_RESOURCE_PERMISSION, String.class);
                        UserPermission requiredUserPermission = new UserPermission(endpointResourceName, endpointResourcePermission);
                        return userPermissions.stream()
                                // if none matches then the request is forbidden
                                .noneMatch(permission -> isRequestAllowed.test(permission, requiredUserPermission));
                    })
                    .to("direct:request-forbidden")
                .endChoice();
    }

}

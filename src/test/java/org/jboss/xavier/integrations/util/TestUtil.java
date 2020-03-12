package org.jboss.xavier.integrations.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.jboss.xavier.integrations.rbac.Acl;
import org.jboss.xavier.integrations.rbac.RbacResponse;

import java.util.Base64;
import java.util.Collections;

public class TestUtil
{
    public static final String HEADER_RH_IDENTITY = "x-rh-identity";

    public static void startUsernameRoutes(CamelContext camelContext) throws Exception
    {
        camelContext.startRoute("request-forbidden");
        camelContext.startRoute("request-notfound");
        camelContext.startRoute("add-username-header");
        camelContext.startRoute("check-authenticated-request");
        camelContext.startRoute("fetch-rbac-user-access");
        camelContext.startRoute("fetch-and-process-rbac-user-access");
        camelContext.startRoute("check-authorized-request");
        camelContext.startRoute("check-rbac-permissions");
    }

    public static String getBase64RHIdentity()
    {
        return Base64.getEncoder().encodeToString("{\"entitlements\":{\"insights\":{\"is_entitled\":true},\"openshift\":{\"is_entitled\":true},\"smart_management\":{\"is_entitled\":false},\"hybrid_cloud\":{\"is_entitled\":true}},\"identity\":{\"internal\":{\"auth_time\":0,\"auth_type\":\"jwt-auth\",\"org_id\":\"6340056\"},\"account_number\":\"1460290\",\"user\":{\"first_name\":\"Marco\",\"is_active\":true,\"is_internal\":true,\"last_name\":\"Rizzi\",\"locale\":\"en_US\",\"is_org_admin\":false,\"username\":\"mrizzi@redhat.com\",\"email\":\"mrizzi+qa@redhat.com\"},\"type\":\"User\"}}".getBytes());
    }

    public static String getBase64RHIdentity(String username)
    {
        return Base64.getEncoder().encodeToString(("{\"entitlements\":{\"insights\":{\"is_entitled\":true},\"openshift\":{\"is_entitled\":true},\"smart_management\":{\"is_entitled\":false},\"hybrid_cloud\":{\"is_entitled\":true}},\"identity\":{\"internal\":{\"auth_time\":0,\"auth_type\":\"jwt-auth\",\"org_id\":\"6340056\"},\"account_number\":\"1460290\",\"user\":{\"first_name\":\"Marco\",\"is_active\":true,\"is_internal\":true,\"last_name\":\"Rizzi\",\"locale\":\"en_US\",\"is_org_admin\":false,\"username\":\"" + username + "\",\"email\":\"mrizzi+qa@redhat.com\"},\"type\":\"User\"}}").getBytes());
    }

    public static void mockRBACResponse(CamelContext camelContext) throws Exception
    {
        camelContext.getRouteDefinition("fetch-rbac-user-access").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveById("fetch-rbac-user-access-endpoint").replace()
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"))
                        .setBody(exchange -> {
                            RbacResponse rbacResponse = new RbacResponse(
                                    new RbacResponse.Meta(1, 10, 0),
                                    new RbacResponse.Links(null, null, null, null),
                                    Collections.singletonList(
                                            new Acl("migration-analytics:*:*", Collections.emptyList()))
                            );
                            try {
                                return new ObjectMapper().writeValueAsString(rbacResponse);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                                throw new IllegalStateException(e);
                            }
                        });
            }
        });
    }
}

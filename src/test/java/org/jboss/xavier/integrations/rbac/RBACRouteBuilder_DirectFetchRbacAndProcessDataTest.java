package org.jboss.xavier.integrations.rbac;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.jboss.xavier.Application;
import org.jboss.xavier.integrations.route.RouteBuilderExceptionHandler;
import org.jboss.xavier.integrations.route.XavierCamelTest;
import org.jboss.xavier.integrations.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RBACRouteBuilder_DirectFetchRbacAndProcessDataTest extends XavierCamelTest {

    @Value("${camel.component.servlet.mapping.context-path}")
    String camel_context;

    @Before
    public void setup() {
        camel_context = camel_context.substring(0, camel_context.indexOf("*"));
    }

    @Test
    public void rbacRouteBuilder_direct_givenNoRHHeader_shouldReturnForbidden() throws Exception {
        //Given

        //When
        camelContext.start();
        TestUtil.startUsernameRoutes(camelContext);
        camelContext.startRoute("fetch-and-process-rbac-user-access");

        Exchange exchange = camelContext
                .createProducerTemplate()
                .request("direct:fetch-and-process-rbac-user-access", exchange1 -> exchange1.getIn().setHeader(TestUtil.HEADER_RH_IDENTITY, null));

        //Then
        assertThat(exchange).isNotNull();
        assertThat(exchange.getIn().getBody()).isEqualTo("Forbidden");
        camelContext.stop();
    }

    @Test
    public void rbacRouteBuilder_direct_givenRHHeader_shouldNotDeletePreviousHeadersOrBody() throws Exception {
        //Given
        camelContext.getRouteDefinition("fetch-rbac-user-access").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveById("fetch-rbac-user-access-endpoint").replace()
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"))
                        .setBody(exchange -> {
                            RbacResponse rbacResponse = new RbacResponse(
                                    new RbacResponse.Meta(1, 10, 0),
                                    new RbacResponse.Links(null, null, null, null),
                                    Collections.emptyList()
                            );
                            try {
                                return new ObjectMapper().writeValueAsString(rbacResponse);
                            } catch (JsonProcessingException e) {
                                throw new IllegalStateException(e);
                            }
                        });
            }
        });

        //When
        camelContext.start();
        TestUtil.startUsernameRoutes(camelContext);
        camelContext.startRoute("fetch-and-process-rbac-user-access");

        Map<String, Object> headers = new HashMap<>();
        headers.put(TestUtil.HEADER_RH_IDENTITY, TestUtil.getBase64RHIdentity());
        headers.put("headerKey1", "headerValue1");

        Object body = "This is my body";

        Exchange exchange = camelContext
                .createProducerTemplate()
                .request("direct:fetch-and-process-rbac-user-access", exchange1 -> {
                    exchange1.getIn().setHeaders(headers);
                    exchange1.getIn().setBody(body);
                });

        //Then
        assertThat(exchange).isNotNull();
        assertThat(exchange.getIn().getBody()).isEqualTo(body);

        assertThat(exchange.getIn().getHeader(TestUtil.HEADER_RH_IDENTITY)).isEqualTo(TestUtil.getBase64RHIdentity());
        assertThat(exchange.getIn().getHeader("headerKey1")).isEqualTo("headerValue1");

        camelContext.stop();
    }

    @Test
    public void rbacRouteBuilder_direct_givenEmptyAcl_shouldReturnEmptyUserAccess() throws Exception {
        //Given
        camelContext.getRouteDefinition("fetch-rbac-user-access").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveById("fetch-rbac-user-access-endpoint").replace()
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"))
                        .setBody(exchange -> {
                            RbacResponse rbacResponse = new RbacResponse(
                                    new RbacResponse.Meta(1, 10, 0),
                                    new RbacResponse.Links(null, null, null, null),
                                    Collections.emptyList()
                            );
                            try {
                                return new ObjectMapper().writeValueAsString(rbacResponse);
                            } catch (JsonProcessingException e) {
                                throw new IllegalStateException(e);
                            }
                        });
            }
        });

        //When
        camelContext.start();
        TestUtil.startUsernameRoutes(camelContext);
        camelContext.startRoute("fetch-and-process-rbac-user-access");

        Exchange exchange = camelContext
                .createProducerTemplate()
                .request("direct:fetch-and-process-rbac-user-access", exchange1 -> {
                    exchange1.getIn().setHeader(TestUtil.HEADER_RH_IDENTITY, TestUtil.getBase64RHIdentity());

                    // Set manually this header since it comes from the Rest filters
                    exchange1.getIn().setHeader(RouteBuilderExceptionHandler.X_RH_IDENTITY_IS_ORG_ADMIN, false);
                });

        //Then
        assertThat(exchange).isNotNull();

        List<Acl> userPermissions = exchange.getIn().getHeader(RBACRouteBuilder.RBAC_USER_PERMISSIONS, List.class);
        assertThat(userPermissions).isNotNull();
        assertThat(userPermissions).isEmpty();
        camelContext.stop();
    }

    @Test
    public void rbacRouteBuilder_direct_givenAclList_shouldReturnAllUserAccess() throws Exception {
        //Given
        camelContext.getRouteDefinition("fetch-rbac-user-access").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveById("fetch-rbac-user-access-endpoint").replace()
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"))
                        .setBody(exchange -> {
                            RbacResponse rbacResponse = new RbacResponse(
                                    new RbacResponse.Meta(1, 10, 0),
                                    new RbacResponse.Links(null, null, null, null),
                                    Arrays.asList(
                                            new Acl("my-application:*:*", new ArrayList<>()),
                                            new Acl("my-application:resource1:operation1", new ArrayList<>())
                                    )
                            );
                            try {
                                return new ObjectMapper().writeValueAsString(rbacResponse);
                            } catch (JsonProcessingException e) {
                                throw new IllegalStateException(e);
                            }
                        });
            }
        });

        //When
        camelContext.start();
        TestUtil.startUsernameRoutes(camelContext);
        camelContext.startRoute("fetch-and-process-rbac-user-access");

        Exchange exchange = camelContext
                .createProducerTemplate()
                .request("direct:fetch-and-process-rbac-user-access", exchange1 -> {
                    exchange1.getIn().setHeader(TestUtil.HEADER_RH_IDENTITY, TestUtil.getBase64RHIdentity());

                    // Set manually this header since it comes from the Rest filters
                    exchange1.getIn().setHeader(RouteBuilderExceptionHandler.X_RH_IDENTITY_IS_ORG_ADMIN, false);
                });

        //Then
        assertThat(exchange).isNotNull();

        List<UserPermission> userPermissions = exchange.getIn().getHeader(RBACRouteBuilder.RBAC_USER_PERMISSIONS, List.class);
        assertThat(userPermissions).isNotNull();
        assertThat(userPermissions).hasSize(2);
        assertThat(userPermissions).containsOnlyOnce(new UserPermission("resource1","operation1"));
        assertThat(userPermissions).containsOnlyOnce(new UserPermission("*","*"));

        camelContext.stop();
    }
}

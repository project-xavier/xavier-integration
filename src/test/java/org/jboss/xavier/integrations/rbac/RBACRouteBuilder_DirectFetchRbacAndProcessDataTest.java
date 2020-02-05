package org.jboss.xavier.integrations.rbac;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.jboss.xavier.Application;
import org.jboss.xavier.integrations.route.XavierCamelTest;
import org.jboss.xavier.integrations.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
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
                });

        //Then
        assertThat(exchange).isNotNull();

        Map userAccess = (Map) exchange.getIn().getHeader(RBACRouteBuilder.RBAC_USER_ACCESS);
        assertThat(userAccess).isNotNull();
        assertThat(userAccess).isEmpty();
        camelContext.stop();
    }

    @Test
    public void rbacRouteBuilder_direct_givenAsterixAcl_shouldReturnAllUserAccess() throws Exception {
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
                                    Collections.singletonList(
                                            new Acl("my-application:*:*", new ArrayList<>())
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
                });

        //Then
        assertThat(exchange).isNotNull();

        Map<String, Map<String, List<String>>> userRbacAccess = (Map) exchange.getIn().getHeader(RBACRouteBuilder.RBAC_USER_ACCESS);
        assertThat(userRbacAccess).isNotNull();

        // Check all the resources are assigned to user since user has my-application:*:* permissions
        assertThat(userRbacAccess.size()).isEqualTo(ResourceTypes.RESOURCE_TYPES.size());
        for (Map.Entry<String, List<String>> systemResourcesEntry : ResourceTypes.RESOURCE_TYPES.entrySet()) {
            String systemResourceName = systemResourcesEntry.getKey();

            Map<String, List<String>> userRbacResourcesAccess = userRbacAccess.get(systemResourceName);
            assertThat(userRbacResourcesAccess).isNotNull();

            List<String> systemResourceOperations = systemResourcesEntry.getValue();
            for (String systemOperation : systemResourceOperations) {
                assertThat(userRbacResourcesAccess.containsKey(systemOperation)).isTrue();

                List<String> userRbacAllowedResources = userRbacResourcesAccess.get(systemOperation);
                assertThat(userRbacAllowedResources).isNotNull();
                assertThat(userRbacAllowedResources.size()).isEqualTo(1);
                assertThat(userRbacAllowedResources.get(0)).isEqualTo(RBACService.WILDCARD);
            }
        }

        camelContext.stop();
    }
}

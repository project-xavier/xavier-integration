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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RBACRouteBuilder_DirectFetchRbacUserAccessTest extends XavierCamelTest {

    @Value("${camel.component.servlet.mapping.context-path}")
    String camel_context;

    @Before
    public void setup() {
        camel_context = camel_context.substring(0, camel_context.indexOf("*"));
    }

    @Test
    public void rbacRouteBuilder_direct_givenEmptyACLResponseAndNoPagination_shouldReturnEmptyUserAccess() throws Exception {
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
        camelContext.startRoute("fetch-rbac-user-access");

        List userAccess = camelContext.createProducerTemplate().requestBodyAndHeader(
                "direct:fetch-rbac-user-access", null, TestUtil.HEADER_RH_IDENTITY, TestUtil.getBase64RHIdentity(), List.class
        );

        //Then
        assertThat(userAccess).isNotNull();
        assertThat(userAccess).isEmpty();
        camelContext.stop();
    }

    @Test
    public void rbacRouteBuilder_direct_errorCodeResponse_shouldReturnEmptyUserAccess() throws Exception {
        //Given
        camelContext.getRouteDefinition("fetch-rbac-user-access").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveById("fetch-rbac-user-access-endpoint").replace()
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("403"))
                        .setBody(() -> null);
            }
        });

        //When
        camelContext.start();
        TestUtil.startUsernameRoutes(camelContext);
        camelContext.startRoute("fetch-rbac-user-access");

        List userAccess = camelContext.createProducerTemplate().requestBodyAndHeader(
                "direct:fetch-rbac-user-access", null, TestUtil.HEADER_RH_IDENTITY, TestUtil.getBase64RHIdentity(), List.class
        );

        //Then
        assertThat(userAccess).isNotNull();
        assertThat(userAccess).isEmpty();
        camelContext.stop();
    }

    @Test
    public void rbacRouteBuilder_direct_responseWithPagination_shouldReturnFetchMultiplePagesAndEnrichResponse() throws Exception {
        //Given
        camelContext.getRouteDefinition("fetch-rbac-user-access").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveById("fetch-rbac-user-access-endpoint").replace()
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"))
                        .setBody(exchange -> {
                            String nextLink = exchange.getIn().getHeader(RBACRouteBuilder.RBAC_NEXT_LINK, String.class);
                            if (nextLink != null && nextLink.isEmpty()) {
                                nextLink = "/api/rbac/v1/access/?application=my-application&limit=10&offset=10";
                            } else if (nextLink != null && !nextLink.isEmpty()) {
                                nextLink = null;
                            }

                            RbacResponse rbacResponse = new RbacResponse(
                                    new RbacResponse.Meta(10, 10, 0),
                                    new RbacResponse.Links(null, nextLink, null, null),
                                    Arrays.asList(
                                            new Acl("application-name:resource1:read", new ArrayList<>()),
                                            new Acl("application-name:resource1:write", new ArrayList<>())
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
        camelContext.startRoute("fetch-rbac-user-access");

        List userAccess = camelContext.createProducerTemplate().requestBodyAndHeader(
                "direct:fetch-rbac-user-access", null, TestUtil.HEADER_RH_IDENTITY, TestUtil.getBase64RHIdentity(), List.class
        );

        //Then
        assertThat(userAccess).isNotNull();
        assertThat(userAccess.size()).isEqualTo(4);
        camelContext.stop();
    }

}

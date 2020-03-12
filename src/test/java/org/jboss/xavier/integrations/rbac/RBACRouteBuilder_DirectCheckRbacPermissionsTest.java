package org.jboss.xavier.integrations.rbac;

import org.apache.camel.Exchange;
import org.jboss.xavier.Application;
import org.jboss.xavier.integrations.route.XavierCamelTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RBACRouteBuilder_DirectCheckRbacPermissionsTest extends XavierCamelTest {

    @Value("${camel.component.servlet.mapping.context-path}")
    String camel_context;

    @Before
    public void setup() {
        camel_context = camel_context.substring(0, camel_context.indexOf("*"));
    }

    @Test
    public void rbacRouteBuilder_direct_checkRbacPermissions_givenNullRbacAccess_shouldAllowContinueProcess() throws Exception {
        //Given
        String previousExchangeBody = "myBody"; // represents the body that were set before calling to direct:check-rbac-permissions
        List<Acl> userPermissions = null;

        //When
        camelContext.start();
        camelContext.startRoute("check-rbac-permissions");

        Exchange exchange = camelContext
                .createProducerTemplate()
                .request("direct:check-rbac-permissions", exchange1 -> {
                    exchange1.getIn().setBody(previousExchangeBody);
                    exchange1.getIn().setHeader(RBACRouteBuilder.RBAC_USER_PERMISSIONS, userPermissions);
                });

        //Then
        assertThat(exchange).isNotNull();
        assertThat(exchange.getIn().getBody()).isEqualTo(previousExchangeBody); // Nothing should've changed
        camelContext.stop();
    }

    @Test
    public void rbacRouteBuilder_direct_checkRbacPermissions_givenAsterixAndAsterix_shouldAllowContinueProcess() throws Exception {
        //Given
        String previousExchangeBody = "myBody"; // represents the body that were set before calling to direct:check-rbac-permissions
        List<UserPermission> userPermissions = new ArrayList<>(1);
        userPermissions.add(new UserPermission("*", "*"));

        //When
        camelContext.start();
        camelContext.startRoute("check-rbac-permissions");

        Exchange exchange = camelContext
                .createProducerTemplate()
                .request("direct:check-rbac-permissions", exchange1 -> {
                    exchange1.getIn().setBody(previousExchangeBody);
                    exchange1.getIn().setHeader(RBACRouteBuilder.RBAC_USER_PERMISSIONS, userPermissions);
                });

        //Then
        assertThat(exchange).isNotNull();
        assertThat(exchange.getIn().getBody()).isEqualTo(previousExchangeBody); // Nothing should've changed
        camelContext.stop();
    }

    @Test
    public void rbacRouteBuilder_direct_checkRbacPermissions_givenUserAccessAndInvalidResourceName_shouldReturnForbidden() throws Exception {
        // User has access to 'myResource' but not to 'otherResource'

        //Given
        List<UserPermission> userPermissions = new ArrayList<>(2);
        userPermissions.add(new UserPermission("myResource", "read"));
        userPermissions.add(new UserPermission("myResource", "write"));

        String endpointResourceName = "otherResource"; // Should be configured in the rest camel endpoint
        String endpointPermission = "read"; // Should be configured in the rest camel endpoint

        //When
        camelContext.start();
        camelContext.startRoute("request-forbidden");
        camelContext.startRoute("check-rbac-permissions");

        Exchange exchange = camelContext
                .createProducerTemplate()
                .request("direct:check-rbac-permissions", exchange1 -> {
                    exchange1.getIn().setBody("my body");
                    exchange1.getIn().setHeader(RBACRouteBuilder.RBAC_USER_PERMISSIONS, userPermissions);
                    exchange1.getIn().setHeader(RBACRouteBuilder.RBAC_ENDPOINT_RESOURCE_NAME, endpointResourceName);
                    exchange1.getIn().setHeader(RBACRouteBuilder.RBAC_ENDPOINT_RESOURCE_PERMISSION, endpointPermission);
                });

        //Then
        assertThat(exchange).isNotNull();
        assertThat(exchange.getIn().getBody()).isEqualTo("Forbidden");
        camelContext.stop();
    }

    @Test
    public void rbacRouteBuilder_direct_checkRbacPermissions_givenUserAccessAndInvalidPermission_shouldReturnForbidden() throws Exception {
        // User has access to 'myResource' and
        // User has access to 'read' and 'write' but not to delete

        //Given
        List<UserPermission> userPermissions = new ArrayList<>(2);
        userPermissions.add(new UserPermission("myResource", "read"));
        userPermissions.add(new UserPermission("myResource", "write"));

        String endpointResourceName = "myResource"; // Should be configured in the rest camel endpoint
        String endpointPermission = "delete"; // Should be configured in the rest camel endpoint

        //When
        camelContext.start();
        camelContext.startRoute("request-forbidden");
        camelContext.startRoute("check-rbac-permissions");

        Exchange exchange = camelContext
                .createProducerTemplate()
                .request("direct:check-rbac-permissions", exchange1 -> {
                    exchange1.getIn().setBody("my body");
                    exchange1.getIn().setHeader(RBACRouteBuilder.RBAC_USER_PERMISSIONS, userPermissions);
                    exchange1.getIn().setHeader(RBACRouteBuilder.RBAC_ENDPOINT_RESOURCE_NAME, endpointResourceName);
                    exchange1.getIn().setHeader(RBACRouteBuilder.RBAC_ENDPOINT_RESOURCE_PERMISSION, endpointPermission);
                });

        //Then
        assertThat(exchange).isNotNull();
        assertThat(exchange.getIn().getBody()).isEqualTo("Forbidden");
        camelContext.stop();
    }

}

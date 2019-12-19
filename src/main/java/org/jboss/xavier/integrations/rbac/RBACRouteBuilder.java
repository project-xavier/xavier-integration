package org.jboss.xavier.integrations.rbac;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RBACRouteBuilder extends RouteBuilder {

    public void configure() throws Exception {
        from("direct:check-allowed-request")
                .routeId("check-allowed-request")
                .setHeader(Exchange.HTTP_QUERY, constant("application=migration-analytics"))
                .log("RBAC");
//                .to("http4://ci.cloud.redhat.com/api/rbac/v1/access?bridgeEndpoint=true");
//                .choice()
//                    .when(header(MainRouteBuilder.USERNAME).isEqualTo(""))
//                        .to("direct:request-forbidden")
//                    .otherwise()
//                        .to("direct:request-forbidden");

    }

}

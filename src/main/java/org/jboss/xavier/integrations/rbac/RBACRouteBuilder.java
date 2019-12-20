package org.jboss.xavier.integrations.rbac;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.jboss.xavier.integrations.route.model.PageBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class RBACRouteBuilder extends RouteBuilder {

    public static final String RBAC_HEADER_NAME = "Rbac";

    public void configure() throws Exception {
        from("direct:check-allowed-request")
                .routeId("check-allowed-request")
                .setHeader(Exchange.HTTP_QUERY, constant("application=migration-analytics"))

                // This a Mock value just for the prototype. This should be replaced by a HTTP request to RBAC
                // .to("http4://ci.cloud.redhat.com/api/rbac/v1/access?bridgeEndpoint=true");
                .process(exchange -> {
                    List<Acl> acls = new ArrayList<>();
                    acls.add(
                            new Acl("migration-analytics:payload:read", Collections.emptyList())
                    );
                    acls.add(
                            new Acl("migration-analytics:payload:write", Collections.singletonList(
                                    new Acl.ResourceDefinition(
                                            new Acl.AttributeFilter("migration-analytics.payload", "in", "1,3,5")
                                    )
                            ))
                    );
                    acls.add(
                            new Acl("migration-analytics:payload:write", Collections.singletonList(
                                    new Acl.ResourceDefinition(
                                            new Acl.AttributeFilter("migration-analytics.payload", "equal", "8")
                                    )
                            ))
                    );

                    // create pagination header
                    Map<String, Object> headers = exchange.getIn().getHeaders();
                    headers.put(RBAC_HEADER_NAME, acls);

                    // store the reply from the bean on the OUT message
                    exchange.getOut().setHeaders(headers);
                    exchange.getOut().setBody(exchange.getIn().getBody());
                    exchange.getOut().setAttachments(exchange.getIn().getAttachments());
                });

    }

}

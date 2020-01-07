package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.jboss.xavier.Application;
import org.jboss.xavier.integrations.rbac.Acl;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class})
@ActiveProfiles("test")
public abstract class XavierCamelTest {
    @Autowired
    protected CamelContext camelContext;

    @Value("${insights.rbac.applicationName}")
    private String rbacApplicationName;

    @Before
    public void beforeTest() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);
        camelContext.addComponent("aws-s3", camelContext.getComponent("stub"));

        camelContext.getRouteDefinition("fetch-and-process-rbac-user-access").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveById("rbac-server-access-endpoint").replace()
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"))
                        .setBody(exchange -> {
                            List<Acl> acls = new ArrayList<>();
                            acls.add(
                                    new Acl(rbacApplicationName + ":*:*", Collections.emptyList())
                            );
                            return acls;
                        });
            }
        });
    }
}

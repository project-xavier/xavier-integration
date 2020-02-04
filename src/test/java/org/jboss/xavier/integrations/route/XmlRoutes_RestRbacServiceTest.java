package org.jboss.xavier.integrations.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.jboss.xavier.Application;
import org.jboss.xavier.integrations.jpa.service.UserService;
import org.jboss.xavier.integrations.rbac.Acl;
import org.jboss.xavier.integrations.rbac.RbacResponse;
import org.jboss.xavier.integrations.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.junit.Ignore; @Ignore // @Ignore
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XmlRoutes_RestRbacServiceTest extends XavierCamelTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${camel.component.servlet.mapping.context-path}")
    String camel_context;

    @SpyBean
    private UserService userService;

    @Before
    public void setup() {
        camel_context = camel_context.substring(0, camel_context.indexOf("*"));
    }

    @Test
    public void xmlRouteBuilder_GivenAllAccess_shouldReturnUser() throws Exception {
        //Given


        //When
        camelContext.start();
        TestUtil.mockRBACResponse(camelContext);
        TestUtil.startUsernameRoutes(camelContext);
        camelContext.startRoute("get-user-info");

        HttpHeaders headers = new HttpHeaders();
        headers.set(TestUtil.HEADER_RH_IDENTITY, TestUtil.getBase64RHIdentity());

        HttpEntity<Map> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(camel_context + "/user", HttpMethod.GET, entity, String.class);

        //Then
        verify(userService).findUser("mrizzi@redhat.com");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).contains("{\"firstTimeCreatingReports\":true}");

        camelContext.stop();
    }

    @Test
    public void xmlRouteBuilder_GivenEmptyAccess_shouldReturnForbidden() throws Exception {
        //Given


        //When
        camelContext.start();
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
                                e.printStackTrace();
                                throw new IllegalStateException(e);
                            }
                        });
            }
        });
        TestUtil.startUsernameRoutes(camelContext);
        camelContext.startRoute("get-user-info");

        HttpHeaders headers = new HttpHeaders();
        headers.set(TestUtil.HEADER_RH_IDENTITY, TestUtil.getBase64RHIdentity());

        HttpEntity<Map> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(camel_context + "/user", HttpMethod.GET, entity, String.class);

        //Then
        verify(userService, never()).findUser("mrizzi@redhat.com");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(403);

        camelContext.stop();
    }

}

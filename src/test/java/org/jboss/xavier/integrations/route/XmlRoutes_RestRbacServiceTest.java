package org.jboss.xavier.integrations.route;

import org.jboss.xavier.Application;
import org.jboss.xavier.integrations.jpa.service.GreetingsService;
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

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XmlRoutes_RestRbacServiceTest extends XavierCamelTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${camel.component.servlet.mapping.context-path}")
    String camel_context;

    @SpyBean
    private GreetingsService greetingsService;

    @Before
    public void setup() {
        camel_context = camel_context.substring(0, camel_context.indexOf("*"));
    }

    @Test
    public void xmlRouteBuilder_RestGreetings() throws Exception {
        //Given


        //When
        camelContext.start();
        TestUtil.mockRBACResponse(camelContext);
        TestUtil.startUsernameRoutes(camelContext);
        camelContext.startRoute("my-endpoint-route");

        Map<String, String> variables = new HashMap<>();
        String queryParamKey = "queryParamKey";
        String queryParamValue = "queryParamValue";
        variables.put("queryParamKey", queryParamValue);

        HttpHeaders headers = new HttpHeaders();
        headers.set(TestUtil.HEADER_RH_IDENTITY, TestUtil.getBase64RHIdentity());

        Map<String, String> body = new HashMap<>();
        body.put("key1", "value1");
        body.put("key2", "value2");

        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(camel_context + "/greetings?queryParamKey={queryParamKey}", HttpMethod.POST, entity, String.class, variables);

        //Then
        verify(greetingsService).greetings("mrizzi@redhat.com", variables.get(queryParamKey), body);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).contains("\"Greetings to username=" + "mrizzi@redhat.com" + " queryValue=" + queryParamValue + " body=" + body + "\"");

        camelContext.stop();
    }

}

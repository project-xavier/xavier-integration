package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.camel.test.spring.UseAdviceWith;
import org.jboss.xavier.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.inject.Inject;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("direct:store")
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class})
@ActiveProfiles("test")
public class MainRouteBuilder_GetRHIdentity
{
    @Autowired
    CamelContext camelContext;

    @Inject
    MainRouteBuilder mainRouteBuilder;

    @Test
    public void mainRouteBuilder_getRHIdentity_ContentGiven_ShouldAddHeadersAndReturnOriginalObjectEnriched() throws Exception {
        //Given
        String x_rh_identity= Base64.getEncoder().encodeToString("{\"entitlements\":{\"insights\":{\"is_entitled\":true},\"openshift\":{\"is_entitled\":true},\"smart_management\":{\"is_entitled\":false},\"hybrid_cloud\":{\"is_entitled\":true}},\"identity\":{\"internal\":{\"auth_time\":0,\"auth_type\":\"jwt-auth\",\"org_id\":\"6340056\"},\"account_number\":\"1460290\",\"user\":{\"first_name\":\"Marco\",\"is_active\":true,\"is_internal\":true,\"last_name\":\"Rizzi\",\"locale\":\"en_US\",\"is_org_admin\":false,\"username\":\"mrizzi@redhat.com\",\"email\":\"mrizzi+qa@redhat.com\"},\"type\":\"User\"}}".getBytes());

        String filename = "mificherito.txt";
        Map<String, Object> headers = new HashMap<>();
        Map<String, String> ma_metadata = new HashMap<>();
        ma_metadata.put("dummy", "8899");
        ma_metadata.put(MainRouteBuilder.ANALYSIS_ID, "3");
        headers.put("MA_metadata", ma_metadata);
        String rhIdentity = mainRouteBuilder.getRHIdentity(x_rh_identity, filename, headers);

        String rhIdentityExpected = "{\"entitlements\":{\"insights\":{\"is_entitled\":true},\"openshift\":{\"is_entitled\":true},\"smart_management\":{\"is_entitled\":false},\"hybrid_cloud\":{\"is_entitled\":true}},\"identity\":{\"internal\":{\"auth_time\":0,\"auth_type\":\"jwt-auth\",\"org_id\":\"6340056\",\"filename\":\"mificherito.txt\",\"dummy\":\"8899\",\"analysisId\":\"3\"},\"account_number\":\"1460290\",\"user\":{\"first_name\":\"Marco\",\"is_active\":true,\"is_internal\":true,\"last_name\":\"Rizzi\",\"locale\":\"en_US\",\"is_org_admin\":false,\"username\":\"mrizzi@redhat.com\",\"email\":\"mrizzi+qa@redhat.com\"},\"type\":\"User\"}}";
        assertThat(new String(Base64.getDecoder().decode(rhIdentity))).isEqualToIgnoringCase(rhIdentityExpected);
    }
}

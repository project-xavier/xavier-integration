package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.camel.test.spring.UseAdviceWith;
import org.jboss.xavier.Application;
import org.jboss.xavier.integrations.route.model.WorkloadInventoryFilterBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("")
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class})
@ActiveProfiles("test")
public class ToBeanRouter_DirectWorkloadInventoryFilterBean {

    @Autowired
    CamelContext camelContext;

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenNoValidHeaders_ShouldAddFilterHeader() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);

        Map<String, Object> headers = new HashMap<>();
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-workloadInventoryFilterBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-workloadInventoryFilterBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        assertThat(routeExchange.getIn().getBody()).isEqualTo(routeExchange.getOut().getBody());
        assertThat(routeExchange.getOut().getHeaders().entrySet()).containsAll(headers.entrySet());

        Object filterHeader = routeExchange.getOut().getHeaders().get(ToBeanRouter.WORKLOAD_INVENTORY_FILTER_HEADER_NAME);
        assertThat(filterHeader).isInstanceOf(WorkloadInventoryFilterBean.class);

        WorkloadInventoryFilterBean filterBean = (WorkloadInventoryFilterBean) filterHeader;
        assertThat(filterBean.getProvider()).isNull();
        assertThat(filterBean.getDatacenter()).isNull();
        assertThat(filterBean.getCluster()).isNull();
        assertThat(filterBean.getVmName()).isNull();
        assertThat(filterBean.getOsName()).isNull();
        assertThat(filterBean.getWorkloads()).isNull();
        assertThat(filterBean.getComplexity()).isNull();
        assertThat(filterBean.getRecommendedTargetsIMS()).isNull();
        assertThat(filterBean.getFlagsIMS()).isNull();

        camelContext.stop();
    }

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenHeaders_ShouldAddPaginationHeader() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);

        Map<String, Object> headers = new HashMap<>();
        headers.put("anotherHeader", "my custom header value");

        String provider = "my provider";
        headers.put("provider", provider);

        String cluster = "my cluster";
        headers.put("cluster", cluster);

        String datacenter = "my datacenter";
        headers.put("datacenter", datacenter);

        String vmName = "my vmName";
        headers.put("vmName", vmName);

        String osName = "my osName";
        headers.put("osName", osName);

        Set<String> workloads = new HashSet<>(Arrays.asList("my workload1", "my workload2"));
        headers.put("workloads", workloads);

        Set<String> recommendedTargetsIMS = new HashSet<>(Arrays.asList("my recommendedTarget1", "my recommendedTarget2"));
        headers.put("recommendedTargetsIMS", recommendedTargetsIMS);

        Set<String> flagsIMS = new HashSet<>(Arrays.asList("my flags1", "my flags2"));
        headers.put("flagsIMS", flagsIMS);

        String complexity = "my complexity";
        headers.put("complexity", complexity);

        //When
        camelContext.start();
        camelContext.startRoute("to-workloadInventoryFilterBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-workloadInventoryFilterBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        assertThat(routeExchange.getIn().getBody()).isEqualTo(routeExchange.getOut().getBody());
        assertThat(routeExchange.getOut().getHeaders().entrySet()).containsAll(headers.entrySet());

        Object filterHeader = routeExchange.getOut().getHeaders().get(ToBeanRouter.WORKLOAD_INVENTORY_FILTER_HEADER_NAME);
        assertThat(filterHeader).isInstanceOf(WorkloadInventoryFilterBean.class);

        WorkloadInventoryFilterBean filterBean = (WorkloadInventoryFilterBean) filterHeader;
        assertThat(filterBean.getProvider()).isEqualTo(provider);
        assertThat(filterBean.getDatacenter()).isEqualTo(datacenter);
        assertThat(filterBean.getCluster()).isEqualTo(cluster);
        assertThat(filterBean.getVmName()).isEqualTo(vmName);
        assertThat(filterBean.getOsName()).isEqualTo(osName);
        assertThat(filterBean.getWorkloads()).isEqualTo(workloads);
        assertThat(filterBean.getComplexity()).isEqualTo(complexity);
        assertThat(filterBean.getRecommendedTargetsIMS()).isEqualTo(recommendedTargetsIMS);
        assertThat(filterBean.getFlagsIMS()).isEqualTo(flagsIMS);

        camelContext.stop();
    }

}

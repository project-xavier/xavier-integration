package org.jboss.xavier.integrations.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.jboss.xavier.integrations.route.model.PageBean;
import org.jboss.xavier.integrations.route.model.SortBean;
import org.jboss.xavier.integrations.route.model.WorkloadInventoryFilterBean;
import org.jboss.xavier.utils.ConversionUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ToBeanRouter extends RouteBuilder {

    public static final String PAGE_HEADER_NAME = "pageBean";
    public static final String SORT_HEADER_NAME = "sortBean";
    public static final String WORKLOAD_INVENTORY_FILTER_HEADER_NAME = "workloadInventoryFilterBean";

    @Override
    public void configure() throws Exception {
        from("direct:to-paginationBean")
                .id("to-paginationBean")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        // extract the name parameter from the Camel message which we want to use
                        Object pageHeader = exchange.getIn().getHeader("page");
                        Integer page = ConversionUtils.toInteger(pageHeader);

                        Object sizeHeader = exchange.getIn().getHeader("size");
                        Integer size = ConversionUtils.toInteger(sizeHeader);

                        // create pagination header
                        Map<String, Object> headers = exchange.getIn().getHeaders();
                        headers.put(PAGE_HEADER_NAME, new PageBean(page, size));

                        // store the reply from the bean on the OUT message
                        exchange.getOut().setHeaders(headers);
                        exchange.getOut().setBody(exchange.getIn().getBody());
                    }
                });

        from("direct:to-sortBean")
                .id("to-sortBean")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        // extract the name parameter from the Camel message which we want to use
                        Object orderByHeader = exchange.getIn().getHeader("orderBy");
                        String orderBy = orderByHeader != null ? (String) orderByHeader : null;

                        Object orderAscHeader = exchange.getIn().getHeader("orderAsc");
                        Boolean orderAsc = ConversionUtils.toBoolean(orderAscHeader);

                        // create pagination header
                        Map<String, Object> headers = exchange.getIn().getHeaders();
                        headers.put(SORT_HEADER_NAME, new SortBean(orderBy, orderAsc));

                        // store the reply from the bean on the OUT message
                        exchange.getOut().setHeaders(headers);
                        exchange.getOut().setBody(exchange.getIn().getBody());
                    }
                });

        from("direct:to-workloadInventoryFilterBean")
                .id("to-workloadInventoryFilterBean")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        // extract the name parameter from the Camel message which we want to use
                        Object providerByHeader = exchange.getIn().getHeader("provider");
                        String provider = providerByHeader != null ? (String) providerByHeader : null;

                        Object datacenterHeader = exchange.getIn().getHeader("datacenter");
                        String datacenter = datacenterHeader != null ? (String) datacenterHeader : null;

                        Object clusterHeader = exchange.getIn().getHeader("cluster");
                        String cluster = clusterHeader != null ? (String) clusterHeader : null;

                        Object vmNameHeader = exchange.getIn().getHeader("vmName");
                        String vmName = vmNameHeader != null ? (String) vmNameHeader : null;

                        Object osNameHeader = exchange.getIn().getHeader("osName");
                        String osName = osNameHeader != null ? (String) osNameHeader : null;

                        Object workloadsHeader = exchange.getIn().getHeader("workloads");
                        List<String> workloadsList = ConversionUtils.toList(workloadsHeader);
                        Set<String> workloads = workloadsList != null ? new HashSet<>(workloadsList) : null;

                        Object complexityHeader = exchange.getIn().getHeader("complexity");
                        String complexity = complexityHeader != null ? (String) complexityHeader : null;

                        Object recommendedTargetsHeader = exchange.getIn().getHeader("recommendedTargetsIMS");
                        List<String> recommendedTargetsList = ConversionUtils.toList(recommendedTargetsHeader);
                        Set<String> recommendedTargets = recommendedTargetsList != null ? new HashSet<>(recommendedTargetsList) : null;

                        Object flagsHeader = exchange.getIn().getHeader("flagsIMS");
                        List<String> flagsList = ConversionUtils.toList(flagsHeader);
                        Set<String> flags = flagsList != null ? new HashSet<>(flagsList) : null;

                        // create pagination header
                        WorkloadInventoryFilterBean filterBean = new WorkloadInventoryFilterBean();
                        filterBean.setProvider(provider);
                        filterBean.setDatacenter(datacenter);
                        filterBean.setCluster(cluster);
                        filterBean.setVmName(vmName);
                        filterBean.setOsName(osName);
                        filterBean.setWorkloads(workloads);
                        filterBean.setComplexity(complexity);
                        filterBean.setRecommendedTargetsIMS(recommendedTargets);
                        filterBean.setFlagsIMS(flags);

                        Map<String, Object> headers = exchange.getIn().getHeaders();
                        headers.put(WORKLOAD_INVENTORY_FILTER_HEADER_NAME, filterBean);

                        // store the reply from the bean on the OUT message
                        exchange.getOut().setHeaders(headers);
                        exchange.getOut().setBody(exchange.getIn().getBody());
                    }
                });
    }

}

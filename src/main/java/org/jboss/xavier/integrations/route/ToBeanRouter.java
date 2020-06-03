package org.jboss.xavier.integrations.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jboss.xavier.integrations.route.model.PageBean;
import org.jboss.xavier.integrations.route.model.SortBean;
import org.jboss.xavier.integrations.route.model.WorkloadInventoryFilterBean;
import org.jboss.xavier.utils.ConversionUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ToBeanRouter extends RouteBuilderExceptionHandler {

    public static final String PAGE_HEADER_NAME = "pageBean";
    public static final String SORT_HEADER_NAME = "sortBean";
    public static final String WORKLOAD_INVENTORY_FILTER_HEADER_NAME = "workloadInventoryFilterBean";

    public static final int DEFAULT_OFFSET = 0;
    public static final int MAX_LIMIT = 1000;

    @Override
    public void configure() throws Exception {
        super.configure();

        from("direct:to-pageBean")
                .routeId("to-pageBean")
                .process(exchange -> {
                    Object offsetValue = exchange.getIn().getHeader("offset");
                    Integer offset = ConversionUtils.toInteger(offsetValue);
                    if (offset == null || offset < 0) {
                        offset = DEFAULT_OFFSET;
                    }

                    Object limitValue = exchange.getIn().getHeader("limit");
                    Integer limit = ConversionUtils.toInteger(limitValue);
                    if (limit == null || limit > MAX_LIMIT) {
                        limit = MAX_LIMIT;
                    }

                    exchange.getIn().setHeader(PAGE_HEADER_NAME, new PageBean(offset, limit));
                });

        from("direct:to-sortBean")
                .routeId("to-sortBean")
                .process(exchange -> {
                    // Sort
                    Object sortByValue = exchange.getIn().getHeader("sort_by");

                    // We need to keep the order in which sort_by was defined
                    LinkedHashSet<String> sortByList = new LinkedHashSet<>();
                    if (sortByValue instanceof String) {
                        List<String> collect = Stream.of(((String) sortByValue).split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                        sortByList.addAll(collect);
                    } else if (sortByValue instanceof Collection) {
                        sortByList.addAll((Collection) sortByValue);
                    }

                    List<SortBean> sortByBeans = sortByList.stream().map(sortBy -> {
                        String[] split = sortBy.trim().split(":");
                        String fieldName = !split[0].isEmpty() ? split[0] : null;
                        boolean isAsc = split.length <= 1 || split[1].equalsIgnoreCase("asc");
                        return new SortBean(fieldName, isAsc);
                    }).collect(Collectors.toList());

                    exchange.getIn().setHeader(SORT_HEADER_NAME, sortByBeans);
                });

        from("direct:to-workloadInventoryFilterBean")
                .routeId("to-workloadInventoryFilterBean")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        // extract the name parameter from the Camel message which we want to use
                        Object providerByHeader = exchange.getIn().getHeader("provider");
                        List<String> providerList = ConversionUtils.toList(providerByHeader);
                        Set<String> provider = providerList != null ? new HashSet<>(providerList) : null;

                        Object datacenterHeader = exchange.getIn().getHeader("datacenter");
                        List<String> datacenterList = ConversionUtils.toList(datacenterHeader);
                        Set<String> datacenter = datacenterList != null ? new HashSet<>(datacenterList) : null;

                        Object clusterHeader = exchange.getIn().getHeader("cluster");
                        List<String> clusterList = ConversionUtils.toList(clusterHeader);
                        Set<String> cluster = clusterList != null ? new HashSet<>(clusterList) : null;

                        Object vmNameHeader = exchange.getIn().getHeader("vmName");
                        List<String> vmNameList = ConversionUtils.toList(vmNameHeader);
                        Set<String> vmName = vmNameList != null ? new HashSet<>(vmNameList) : null;

                        Object osNameHeader = exchange.getIn().getHeader("osName");
                        List<String> osNameList = ConversionUtils.toList(osNameHeader);
                        Set<String> osName = osNameList != null ? new HashSet<>(osNameList) : null;

                        Object workloadsHeader = exchange.getIn().getHeader("workload");
                        List<String> workloadsList = ConversionUtils.toList(workloadsHeader);
                        Set<String> workloads = workloadsList != null ? new HashSet<>(workloadsList) : null;

                        Object complexityHeader = exchange.getIn().getHeader("complexity");
                        List<String> complexityList = ConversionUtils.toList(complexityHeader);
                        Set<String> complexity = complexityList != null ? new HashSet<>(complexityList) : null;

                        Object recommendedTargetsHeader = exchange.getIn().getHeader("recommendedTargetIMS");
                        List<String> recommendedTargetsList = ConversionUtils.toList(recommendedTargetsHeader);
                        Set<String> recommendedTargets = recommendedTargetsList != null ? new HashSet<>(recommendedTargetsList) : null;

                        Object flagsHeader = exchange.getIn().getHeader("flagIMS");
                        List<String> flagsList = ConversionUtils.toList(flagsHeader);
                        Set<String> flags = flagsList != null ? new HashSet<>(flagsList) : null;

                        // create pagination header
                        WorkloadInventoryFilterBean filterBean = new WorkloadInventoryFilterBean();
                        filterBean.setProviders(provider);
                        filterBean.setDatacenters(datacenter);
                        filterBean.setClusters(cluster);
                        filterBean.setVmNames(vmName);
                        filterBean.setOsNames(osName);
                        filterBean.setWorkloads(workloads);
                        filterBean.setComplexities(complexity);
                        filterBean.setRecommendedTargetsIMS(recommendedTargets);
                        filterBean.setFlagsIMS(flags);

                        Map<String, Object> headers = exchange.getIn().getHeaders();
                        headers.put(WORKLOAD_INVENTORY_FILTER_HEADER_NAME, filterBean);

                        // store the reply from the bean on the OUT message
                        exchange.getOut().setHeaders(headers);
                        exchange.getOut().setBody(exchange.getIn().getBody());
                        exchange.getOut().setAttachments(exchange.getIn().getAttachments());
                    }
                });
    }

}

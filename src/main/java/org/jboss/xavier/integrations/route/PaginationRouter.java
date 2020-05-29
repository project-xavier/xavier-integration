package org.jboss.xavier.integrations.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.xavier.integrations.jpa.OffsetLimitRequest;
import org.jboss.xavier.integrations.route.model.PageResponse;
import org.jboss.xavier.utils.ConversionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PaginationRouter extends RouteBuilder {

    public static final String PAGE_HEADER_NAME = "pageable";
    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 1000;

    @Value("${camel.component.servlet.mapping.context-path}")
    private String contextPath;

    @Override
    public void configure() throws Exception {
        from("direct:add-pageable-header")
                .routeId("add-pageable-header")
                .process(exchange -> {
                    // Sort
                    Object sortByValue = exchange.getIn().getHeader("sort_by");
                    String sortByDefaults = exchange.getIn().getHeader("sort_by_defaults", String.class);

                    Set<String> sortByList = new HashSet<>();
                    if (sortByValue instanceof String) {
                        String stringValue = (String) sortByValue;
                        if (!stringValue.trim().isEmpty()) {
                            sortByList.add((String) sortByValue);
                        }
                    } else if (sortByValue instanceof Collection){
                        sortByList.addAll((Collection) sortByValue);
                    } else if (sortByValue == null && sortByDefaults != null && !sortByDefaults.trim().isEmpty()) {
                        sortByList = Stream.of(sortByDefaults.split(","))
                                .map(String::trim)
                                .collect(Collectors.toSet());
                    }

                    List<Sort.Order> fieldSorts = sortByList.stream().map(sortBy -> {
                        String[] split = sortBy.trim().split(":");
                        String fieldName = !split[0].isEmpty() ? split[0] : null;
                        boolean isAsc = split.length <= 1 || split[1].equalsIgnoreCase("asc");

                        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
                        return new Sort.Order(direction, fieldName);
                    }).collect(Collectors.toList());

                    Sort sort = null;
                    if (!fieldSorts.isEmpty()) {
                        sort = new Sort(fieldSorts);
                    }

                    // Pageable
                    Object offsetValue = exchange.getIn().getHeader("offset");
                    Integer offset = ConversionUtils.toInteger(offsetValue);
                    if (offset == null) {
                        offset = DEFAULT_OFFSET;
                    }

                    Object limitValue = exchange.getIn().getHeader("limit");
                    Integer limit = ConversionUtils.toInteger(limitValue);
                    if (limit == null) {
                        limit = DEFAULT_LIMIT;
                    }

                    Pageable pageable = new OffsetLimitRequest(offset, limit, sort);
                    exchange.getIn().setHeader(PAGE_HEADER_NAME, pageable);
                });

        from("direct:to-pageable-response")
                .routeId("to-pageable-response")
                .process(exchange -> {
                    Object queryParametersValue = exchange.getIn().getHeader("queryParameters");
                    Set<String> queryParameters = getQueryParameters(queryParametersValue);

                    // Add default query parameters
                    queryParameters.add("sort_by");
                    queryParameters.add("limit");

                    // Link
                    List<NameValuePair> params = new ArrayList<>();
                    for (String key : queryParameters) {
                        Object value = exchange.getIn().getHeader(key);
                        List<String> providerList = ConversionUtils.toList(value);
                        Set<String> provider = providerList != null ? new HashSet<>(providerList) : Collections.emptySet();
                        provider.forEach(f -> params.add(new BasicNameValuePair(key, f)));
                    }


                    String endpointBasePath = exchange.getIn().getHeader(Exchange.HTTP_PATH, String.class);
                    Page<Object> page = exchange.getIn().getBody(Page.class);

                    PageResponse.Links links = new PageResponse.Links();
                    links.setFirst(getLink(endpointBasePath, params, 0));

                    int offsetLast = (page.getTotalPages() - 1) * page.getSize();
                    links.setLast(getLink(endpointBasePath, params, offsetLast));

                    int offsetPrevious = (page.getNumber() - 1) * page.getSize();
                    if (offsetPrevious >= 0) {
                        links.setPrevious(getLink(endpointBasePath, params, offsetPrevious));
                    }

                    int offsetNext = (page.getNumber() + 1) * page.getSize();
                    if (offsetNext <= offsetLast) {
                        links.setNext(getLink(endpointBasePath, params, offsetNext));
                    }

                    // Meta
                    PageResponse.Meta meta = new PageResponse.Meta();
                    meta.setLimit(page.getSize());
                    meta.setOffset(page.getNumber() * page.getSize());
                    meta.setCount(page.getTotalElements());

                    // Result
                    PageResponse<Object> pageResponse = new PageResponse<>();
                    pageResponse.setMeta(meta);
                    pageResponse.setData(page.getContent());
                    pageResponse.setLinks(links);


                    exchange.getIn().setBody(pageResponse);
                });
    }

    private String getLink(String endpointPath, List<NameValuePair> params, int offset) {
        String basePath = contextPath.substring(0, contextPath.length() - 2); // to remove the last * char and last '/'

        List<NameValuePair> copyParams = new ArrayList<>(params);
        copyParams.add(new BasicNameValuePair("offset", String.valueOf(offset)));
        return basePath + endpointPath + "?" + URLEncodedUtils.format(copyParams, "UTF-8");
    }

    private Set<String> getQueryParameters(Object value) {
        if (value == null) {
            return new HashSet<>();
        } else if (value instanceof Collection) {
            Collection<String> valueCollection = (Collection<String>) value;
            return new HashSet<>(valueCollection);
        } else if (value instanceof String) {
            String valueString = (String) value;
            return Stream.of(valueString.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
        } else {
            throw new IllegalStateException("Unsupported queryParameter type=" + value.getClass());
        }
    }

}

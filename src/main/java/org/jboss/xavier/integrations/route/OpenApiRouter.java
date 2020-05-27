package org.jboss.xavier.integrations.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.xavier.integrations.route.model.PageResponse;
import org.jboss.xavier.utils.ConversionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class OpenApiRouter extends RouteBuilder {

    @Value("${camel.component.servlet.mapping.context-path}")
    private String contextPath;

    @Override
    public void configure() throws Exception {
        from("direct:to-openapi-paginationResponse")
                .routeId("to-openapi-paginationResponse")
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

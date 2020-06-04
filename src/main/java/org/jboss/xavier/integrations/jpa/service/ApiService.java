package org.jboss.xavier.integrations.jpa.service;

import org.apache.camel.Exchange;
import org.jboss.xavier.integrations.route.model.PageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class ApiService {

    @Value("${camel.component.servlet.mapping.context-path}")
    String contextPath;

    public <T> PageResponse<T> mapToCustomPage(Exchange exchange, Class<T> tClass) {
        Page<T> page = (Page<T>) exchange.getIn().getBody();

        // LINKS
        String basePath = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        String queryParameters = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
        if (queryParameters == null) {
            queryParameters = "";
        }
        String queryParametersWithoutLimitNorOffset = Arrays.stream(queryParameters.split("&"))
                .filter(e -> !e.startsWith("limit") && !e.startsWith("offset"))
                .collect(Collectors.joining("&"));

        PageResponse.Links links = new PageResponse.Links();

        // First
        int offsetFirst = 0;
        links.setFirst(getURLLink(basePath, queryParametersWithoutLimitNorOffset, page.getSize(), offsetFirst));

        // Last
        int offsetLast;
        if (page.getTotalElements() == page.getNumberOfElements()) { // In case 'limit' is greater than dataset size
            offsetLast = 0;
        } else {
            offsetLast = (page.getTotalPages() - 1) * page.getSize();
        }
        links.setLast(getURLLink(basePath, queryParametersWithoutLimitNorOffset, page.getSize(), offsetLast));

        // Previous
        int offsetPrevious = (page.getNumber() - 1) * page.getSize();
        if (offsetPrevious >= 0) {
            links.setPrev(getURLLink(basePath, queryParametersWithoutLimitNorOffset, page.getSize(), offsetPrevious));
        }

        // Next
        int offsetNext = (page.getNumber() + 1) * page.getSize();
        if (offsetNext <= offsetLast) {
            links.setNext(getURLLink(basePath, queryParametersWithoutLimitNorOffset, page.getSize(), offsetNext));
        }

        // META
        PageResponse.Meta meta = new PageResponse.Meta();
        meta.setLimit(page.getSize());
        meta.setOffset(page.getNumber() * page.getSize());
        meta.setCount(page.getTotalElements());

        // RESULT
        PageResponse<T> result = new PageResponse<>();
        result.setMeta(meta);
        result.setData(page.getContent());
        result.setLinks(links);

        return result;
    }

    public PageResponse<Object> mapToCustomPage(Exchange exchange) {
        return mapToCustomPage(exchange, Object.class);
    }

    private String getURLLink(String basePath, String queryParameters, int limit, int offset) {
        String url = basePath + "?" + queryParameters;
        if (url.endsWith("?")) {
            return MessageFormat.format("{0}limit={1}&offset={2}", url, String.valueOf(limit), String.valueOf(offset));
        } else {
            return MessageFormat.format("{0}&limit={1}&offset={2}", url, String.valueOf(limit), String.valueOf(offset));
        }
    }

}

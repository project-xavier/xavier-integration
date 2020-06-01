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

    public PageResponse<Object> mapToCustomPage(Exchange exchange) {
        Page<Object> page = (Page<Object>) exchange.getIn().getBody();

        // LINKS
        String basePath = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        String queryParameters = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
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
            links.setPrevious(getURLLink(basePath, queryParametersWithoutLimitNorOffset, page.getSize(), offsetPrevious));
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
        PageResponse<Object> result = new PageResponse<>();
        result.setMeta(meta);
        result.setData(page.getContent());
        result.setLinks(links);

        return result;
    }

    private String getURLLink(String basePath, String queryParameters, int limit, int offset) {
        String url = basePath + "?" + queryParameters;
        if (url.endsWith("?")) {
            return MessageFormat.format("{0}limit={1}&offset={2}", url, limit, offset);
        } else {
            return MessageFormat.format("{0}&limit={1}&offset={2}", url, limit, offset);
        }
    }

}

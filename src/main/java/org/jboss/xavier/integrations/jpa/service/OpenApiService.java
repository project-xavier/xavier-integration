package org.jboss.xavier.integrations.jpa.service;

import org.apache.camel.Exchange;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.xavier.integrations.route.model.PageResponse;
import org.jboss.xavier.utils.ConversionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OpenApiService {

//    @Value("${camel.component.servlet.mapping.context-path}")
//    private String contextPath;

    public <T> PageResponse<T> buildResponse(Map<String, Object> headers, Page<T> page, Set<String> queryParameters) {
        if (queryParameters == null) {
            queryParameters = new HashSet<>();
        }

        queryParameters.add("sort_by");
        queryParameters.add("limit");

        // Link
        List<NameValuePair> params = new ArrayList<>();
        for (String key : queryParameters) {
            Object value = headers.get(key);
            List<String> providerList = ConversionUtils.toList(value);
            Set<String> provider = providerList != null ? new HashSet<>(providerList) : Collections.emptySet();
            provider.forEach(f -> params.add(new BasicNameValuePair(key, f)));
        }

        String endpointBasePath = (String) headers.get(Exchange.HTTP_PATH);

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
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setMeta(meta);
        pageResponse.setData(page.getContent());
        pageResponse.setLinks(links);

        return pageResponse;
    }

    private String getLink(String endpointPath, List<NameValuePair> params, int offset) {
        List<NameValuePair> copyParams = new ArrayList<>(params);
        copyParams.add(new BasicNameValuePair("offset", String.valueOf(offset)));

//        String basePath = contextPath.substring(0, contextPath.length() - 1); // to remove the last * char
        return endpointPath + "?" + URLEncodedUtils.format(copyParams, "UTF-8");
    }
}

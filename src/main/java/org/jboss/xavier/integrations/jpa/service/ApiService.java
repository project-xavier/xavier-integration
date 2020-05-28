package org.jboss.xavier.integrations.jpa.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ApiService {
    @Value("${camel.component.servlet.mapping.context-path}")
    String contextPath;

   /*
   {"data":[
       {"flag":"flag","osName":"osName","flagLabel":"flaglabel1","assessment":"assessment1"},
       {"flag":"flag2","osName":"osName2","flagLabel":"flaglabel2","assessment":"assessment2"},
       {"flag":"flag3","osName":"osName3","flagLabel":"flaglabel3","assessment":"assessment3"},
       {"flag":"flag4","osName":"osName4","flagLabel":"flaglabel4","assessment":"assessment4"}],
     "meta":{"offset":0,"count":0,"limit":10},
     "links":{"last":"/api/xavier//api/xavier/mappings/flag-assessment","first":"/api/xavier//api/xavier/mappings/flag-assessment"}}
     */

    public Map<String,Object> mapToCustomPage(Exchange exchange) {
        Page<Object> page = (Page<Object>) exchange.getIn().getBody();
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> meta = new HashMap<>();
        meta.put("count", page.getTotalElements());
        meta.put("limit", page.getSize());
        meta.put("offset", page.getNumber());
        result.put("meta", meta);

        result.put("data", page.getContent());

        Map<String, Object> links = new HashMap<>();
        links.put("first", getURLLink(exchange.getIn().getHeader(Exchange.HTTP_URI, String.class), 0, page.getSize()));
        links.put("last", getURLLink(exchange.getIn().getHeader(Exchange.HTTP_URI, String.class), page.getTotalPages() -1, page.getSize()));
        result.put("links", links);

        return result;
    }

    private String getURLLink(String url, int offset, int limit) {
        int indexOfQuestion = url.indexOf("?");
        
        if (indexOfQuestion >= 0) {
          String query = url.substring(indexOfQuestion);
          String urlNoParams = url.substring(0, indexOfQuestion);
          String params = Arrays.asList(query.split("&")).stream().filter(e -> !e.startsWith("limit") && !e.startsWith("offset")).collect(Collectors.joining());
          url = urlNoParams + ((params.isEmpty()) ? "?" : "?" + params + "&");
        } else {
          url = url + "?";
        }
        url = url + "limit=" + limit;
        url = url + "&offset=" + offset;

        return url;
    }
}
package org.jboss.xavier.integrations.route;

import org.apache.camel.builder.RouteBuilder;
import org.jboss.xavier.integrations.jpa.OffsetLimitRequest;
import org.jboss.xavier.utils.ConversionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PaginationRouter extends RouteBuilder {

    public static final String PAGE_HEADER_NAME = "pageable";
    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 1000;

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
    }

}

package org.jboss.xavier.integrations.route;

import org.apache.camel.Exchange;
import org.junit.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PaginationRouter_Test extends XavierCamelTest {

    @Test
    public void addPageableHeader_givenNullHeaders_shouldUseDefaults() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", null);
        headers.put("offset", null);
        headers.put("limit", null);
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("add-pageable-header");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:add-pageable-header", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object result = routeExchange.getIn().getHeaders().get(PaginationRouter.PAGE_HEADER_NAME);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Pageable.class);

        Pageable pageable = (Pageable) result;
        assertThat(pageable.getOffset()).isEqualTo(PaginationRouter.DEFAULT_OFFSET);
        assertThat(pageable.getPageSize()).isEqualTo(PaginationRouter.DEFAULT_LIMIT);
        assertThat(pageable.getSort()).isNull();

        camelContext.stop();
    }

    @Test
    public void addPageableHeader_GivenSortBySingleHeader_shouldUseDefaultSortAsc() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", "myFieldName");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("add-pageable-header");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:add-pageable-header", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object result = routeExchange.getIn().getHeaders().get(PaginationRouter.PAGE_HEADER_NAME);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Pageable.class);

        Pageable pageable = (Pageable) result;
        assertThat(pageable.getOffset()).isEqualTo(PaginationRouter.DEFAULT_OFFSET);
        assertThat(pageable.getPageSize()).isEqualTo(PaginationRouter.DEFAULT_LIMIT);

        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("myFieldName")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("myFieldName").getDirection()).isEqualTo(Sort.Direction.ASC);

        camelContext.stop();
    }

    @Test
    public void addPageableHeader_GivenSortAscHeader_shouldAddSort() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", "myFieldName:asc");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("add-pageable-header");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:add-pageable-header", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object result = routeExchange.getIn().getHeaders().get(PaginationRouter.PAGE_HEADER_NAME);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Pageable.class);

        Pageable pageable = (Pageable) result;
        assertThat(pageable.getOffset()).isEqualTo(PaginationRouter.DEFAULT_OFFSET);
        assertThat(pageable.getPageSize()).isEqualTo(PaginationRouter.DEFAULT_LIMIT);

        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("myFieldName")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("myFieldName").getDirection()).isEqualTo(Sort.Direction.ASC);

        camelContext.stop();
    }

    @Test
    public void addPageableHeader_GivenSortDescHeader_shouldAddSort() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", "myFieldName:desc");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("add-pageable-header");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:add-pageable-header", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object result = routeExchange.getIn().getHeaders().get(PaginationRouter.PAGE_HEADER_NAME);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Pageable.class);

        Pageable pageable = (Pageable) result;
        assertThat(pageable.getOffset()).isEqualTo(PaginationRouter.DEFAULT_OFFSET);
        assertThat(pageable.getPageSize()).isEqualTo(PaginationRouter.DEFAULT_LIMIT);

        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("myFieldName")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("myFieldName").getDirection()).isEqualTo(Sort.Direction.DESC);

        camelContext.stop();
    }

    @Test
    public void addPageableHeader_GivenMultipleSortHeaders_shouldAddSort() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", Arrays.asList("myFieldName1", "myFieldName2:asc", "myFieldName3:desc"));
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("add-pageable-header");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:add-pageable-header", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object result = routeExchange.getIn().getHeaders().get(PaginationRouter.PAGE_HEADER_NAME);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Pageable.class);

        Pageable pageable = (Pageable) result;
        assertThat(pageable.getOffset()).isEqualTo(PaginationRouter.DEFAULT_OFFSET);
        assertThat(pageable.getPageSize()).isEqualTo(PaginationRouter.DEFAULT_LIMIT);

        assertThat(pageable.getSort().getOrderFor("myFieldName1")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("myFieldName1").getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(pageable.getSort().getOrderFor("myFieldName2")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("myFieldName2").getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(pageable.getSort().getOrderFor("myFieldName3")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("myFieldName3").getDirection()).isEqualTo(Sort.Direction.DESC);

        camelContext.stop();
    }

    @Test
    public void addPageableHeader_GivenSortHeadersAndDefaultSorts_shouldAddSortAndOmitDefaultSorts() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", "myFieldName:desc");
        headers.put("sort_by_defaults", "anotherFieldName:asc");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("add-pageable-header");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:add-pageable-header", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object result = routeExchange.getIn().getHeaders().get(PaginationRouter.PAGE_HEADER_NAME);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Pageable.class);

        Pageable pageable = (Pageable) result;
        assertThat(pageable.getOffset()).isEqualTo(PaginationRouter.DEFAULT_OFFSET);
        assertThat(pageable.getPageSize()).isEqualTo(PaginationRouter.DEFAULT_LIMIT);

        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("myFieldName")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("myFieldName").getDirection()).isEqualTo(Sort.Direction.DESC);

        assertThat(pageable.getSort().getOrderFor("anotherFieldName")).isNull();

        camelContext.stop();
    }

    @Test
    public void addPageableHeader_GivenNullSortHeadersAndDefaultSorts_shouldAddSortAndUsingDefaultSorts() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", null);
        headers.put("sort_by_defaults", "anotherFieldName1:asc, anotherFieldName2:desc");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("add-pageable-header");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:add-pageable-header", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object result = routeExchange.getIn().getHeaders().get(PaginationRouter.PAGE_HEADER_NAME);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Pageable.class);

        Pageable pageable = (Pageable) result;
        assertThat(pageable.getOffset()).isEqualTo(PaginationRouter.DEFAULT_OFFSET);
        assertThat(pageable.getPageSize()).isEqualTo(PaginationRouter.DEFAULT_LIMIT);

        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("anotherFieldName1")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("anotherFieldName1").getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(pageable.getSort().getOrderFor("anotherFieldName2")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("anotherFieldName2").getDirection()).isEqualTo(Sort.Direction.DESC);

        camelContext.stop();
    }

    @Test
    public void addPageableHeader_GivenOffsetAndLimit_shouldAddSort() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("offset", "3");
        headers.put("limit", "7");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("add-pageable-header");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:add-pageable-header", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object result = routeExchange.getIn().getHeaders().get(PaginationRouter.PAGE_HEADER_NAME);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Pageable.class);

        Pageable pageable = (Pageable) result;
        assertThat(pageable.getOffset()).isEqualTo(3);
        assertThat(pageable.getPageSize()).isEqualTo(7);

        assertThat(pageable.getSort()).isNull();

        camelContext.stop();
    }
}

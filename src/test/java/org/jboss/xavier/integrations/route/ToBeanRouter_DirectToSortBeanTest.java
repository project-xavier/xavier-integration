package org.jboss.xavier.integrations.route;

import org.apache.camel.Exchange;
import org.jboss.xavier.integrations.route.model.SortBean;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ToBeanRouter_DirectToSortBeanTest extends XavierCamelTest {

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenNoHeaders_ShouldAddEmptyListOfSortsHeader() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object sortHeader = routeExchange.getIn().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(sortHeader).isNotNull();
        assertThat(sortHeader).isInstanceOf(List.class);

        List<SortBean> sortByBeans = (List<SortBean>) sortHeader;
        assertThat(sortByBeans).isEmpty();

        camelContext.stop();
    }

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenSingleSortByHeader_ShouldAddSortBeanHeader() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", "myColumnName");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object sortHeader = routeExchange.getIn().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(sortHeader).isNotNull();
        assertThat(sortHeader).isInstanceOf(List.class);

        List<SortBean> sortByBeans = (List<SortBean>) sortHeader;
        assertThat(sortByBeans).hasSize(1);
        assertThat(sortByBeans.get(0).getOrderBy()).isEqualTo("myColumnName");
        assertThat(sortByBeans.get(0).isOrderAsc()).isTrue();

        camelContext.stop();
    }

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenNullHeaders_ShouldAddEmptyListOfSortsHeader() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", null);
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object sortHeader = routeExchange.getIn().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(sortHeader).isNotNull();
        assertThat(sortHeader).isInstanceOf(List.class);

        List<SortBean> sortByBeans = (List<SortBean>) sortHeader;
        assertThat(sortByBeans).isEmpty();

        camelContext.stop();
    }

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenStringHeaders_ShouldAddPaginationHeader() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", "myColumnName");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object sortHeader = routeExchange.getIn().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(sortHeader).isNotNull();
        assertThat(sortHeader).isInstanceOf(List.class);

        List<SortBean> sortByBeans = (List<SortBean>) sortHeader;
        assertThat(sortByBeans).hasSize(1);
        assertThat(sortByBeans.get(0).getOrderBy()).isEqualTo("myColumnName");
        assertThat(sortByBeans.get(0).isOrderAsc()).isTrue();

        camelContext.stop();
    }

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenSortAscHeader_shouldAddSort() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", "myColumnName:asc");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object sortHeader = routeExchange.getIn().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(sortHeader).isNotNull();
        assertThat(sortHeader).isInstanceOf(List.class);

        List<SortBean> sortByBeans = (List<SortBean>) sortHeader;
        assertThat(sortByBeans).hasSize(1);
        assertThat(sortByBeans.get(0).getOrderBy()).isEqualTo("myColumnName");
        assertThat(sortByBeans.get(0).isOrderAsc()).isTrue();

        camelContext.stop();
    }

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenSortDescHeader_shouldAddSort() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", "myColumnName:desc");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object sortHeader = routeExchange.getIn().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(sortHeader).isNotNull();
        assertThat(sortHeader).isInstanceOf(List.class);

        List<SortBean> sortByBeans = (List<SortBean>) sortHeader;
        assertThat(sortByBeans).hasSize(1);
        assertThat(sortByBeans.get(0).getOrderBy()).isEqualTo("myColumnName");
        assertThat(sortByBeans.get(0).isOrderAsc()).isFalse();

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
        camelContext.startRoute("to-sortBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object sortHeader = routeExchange.getIn().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(sortHeader).isNotNull();
        assertThat(sortHeader).isInstanceOf(List.class);

        List<SortBean> sortByBeans = (List<SortBean>) sortHeader;
        assertThat(sortByBeans).hasSize(3);
        assertThat(sortByBeans.get(0).getOrderBy()).isEqualTo("myFieldName1");
        assertThat(sortByBeans.get(0).isOrderAsc()).isTrue();
        assertThat(sortByBeans.get(1).getOrderBy()).isEqualTo("myFieldName2");
        assertThat(sortByBeans.get(1).isOrderAsc()).isTrue();
        assertThat(sortByBeans.get(2).getOrderBy()).isEqualTo("myFieldName3");
        assertThat(sortByBeans.get(2).isOrderAsc()).isFalse();

        camelContext.stop();
    }

    @Test
    public void addPageableHeader_GivenSortHeadersUsingComma_shouldAddSort() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", "myFieldName1, myFieldName2:asc, myFieldName3:desc");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object sortHeader = routeExchange.getIn().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(sortHeader).isNotNull();
        assertThat(sortHeader).isInstanceOf(List.class);

        List<SortBean> sortByBeans = (List<SortBean>) sortHeader;
        assertThat(sortByBeans).hasSize(3);
        assertThat(sortByBeans.get(0).getOrderBy()).isEqualTo("myFieldName1");
        assertThat(sortByBeans.get(0).isOrderAsc()).isTrue();
        assertThat(sortByBeans.get(1).getOrderBy()).isEqualTo("myFieldName2");
        assertThat(sortByBeans.get(1).isOrderAsc()).isTrue();
        assertThat(sortByBeans.get(2).getOrderBy()).isEqualTo("myFieldName3");
        assertThat(sortByBeans.get(2).isOrderAsc()).isFalse();

        camelContext.stop();
    }
}

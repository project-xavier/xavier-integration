package org.jboss.xavier.integrations.route;

import org.apache.camel.Exchange;
import org.jboss.xavier.integrations.route.model.SortBean;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ToBeanRouter_DirectToSortBeanTest extends XavierCamelTest {

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenNoHeaders_ShouldAddPaginationHeader() throws Exception {
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
        assertThat(routeExchange.getIn().getBody()).isEqualTo(routeExchange.getOut().getBody());
        assertThat(routeExchange.getOut().getHeaders().entrySet()).containsAll(headers.entrySet());

        Object sortBean = routeExchange.getOut().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(sortBean).isInstanceOf(SortBean.class);

        SortBean pageBean = (SortBean) sortBean;
        assertThat(pageBean.getOrderBy()).isNull();
        assertThat(pageBean.isOrderAsc()).isNull();

        camelContext.stop();
    }

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenHeaders_ShouldAddPaginationHeader() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("orderBy", "myColumnName");
        headers.put("orderAsc", true);
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        assertThat(routeExchange.getIn().getBody()).isEqualTo(routeExchange.getOut().getBody());
        assertThat(routeExchange.getOut().getHeaders().entrySet()).containsAll(headers.entrySet());

        Object paginationHeader = routeExchange.getOut().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(paginationHeader).isInstanceOf(SortBean.class);

        SortBean sortBean = (SortBean) paginationHeader;
        assertThat(sortBean.getOrderBy()).isEqualTo(headers.get("orderBy"));
        assertThat(sortBean.isOrderAsc()).isEqualTo(headers.get("orderAsc"));

        camelContext.stop();
    }

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenNullHeaders_ShouldAddPaginationHeader() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("orderBy", null);
        headers.put("orderAsc", null);
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        assertThat(routeExchange.getIn().getBody()).isEqualTo(routeExchange.getOut().getBody());
        assertThat(routeExchange.getOut().getHeaders().entrySet()).containsAll(headers.entrySet());

        Object paginationHeader = routeExchange.getOut().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(paginationHeader).isInstanceOf(SortBean.class);

        SortBean sortBean = (SortBean) paginationHeader;
        assertThat(sortBean.getOrderBy()).isNull();
        assertThat(sortBean.isOrderAsc()).isNull();

        camelContext.stop();
    }

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenStringHeaders_ShouldAddPaginationHeader() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("orderBy", "myColumnName");
        headers.put("orderAsc", "true");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        assertThat(routeExchange.getIn().getBody()).isEqualTo(routeExchange.getOut().getBody());
        assertThat(routeExchange.getOut().getHeaders().entrySet()).containsAll(headers.entrySet());

        Object paginationHeader = routeExchange.getOut().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(paginationHeader).isInstanceOf(SortBean.class);

        SortBean sortBean = (SortBean) paginationHeader;
        assertThat(sortBean.isOrderAsc()).isEqualTo(true);

        camelContext.stop();
    }

    // V2

    @Test
    public void sortBean2_GivenNullHeaders_ShouldAddNullSortHeader() throws Exception {
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
        assertThat(routeExchange.getIn().getBody()).isEqualTo(routeExchange.getOut().getBody());
        assertThat(routeExchange.getOut().getHeaders().entrySet()).containsAll(headers.entrySet());

        Object result = routeExchange.getOut().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(result).isNull();

        camelContext.stop();
    }

    @Test
    public void sortBean2_GivenHeaders_ShouldAddSortHeader_andUseDefaultAsc() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", "myFieldName");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        assertThat(routeExchange.getIn().getBody()).isEqualTo(routeExchange.getOut().getBody());
        assertThat(routeExchange.getOut().getHeaders().entrySet()).containsAll(headers.entrySet());

        Object result = routeExchange.getOut().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(result).isInstanceOf(SortBean.class);

        SortBean sortBean = (SortBean) result;
        assertThat(sortBean.getOrderBy()).isEqualTo(headers.get("sort_by"));
        assertThat(sortBean.isOrderAsc()).isEqualTo(true);

        camelContext.stop();
    }

    @Test
    public void sortBean2_GivenHeaderWithAsc_ShouldAddSortHeader() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", "myFieldName:asc");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean2");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean2", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        assertThat(routeExchange.getIn().getBody()).isEqualTo(routeExchange.getOut().getBody());
        assertThat(routeExchange.getOut().getHeaders().entrySet()).containsAll(headers.entrySet());

        Object result = routeExchange.getOut().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(result).isInstanceOf(SortBean.class);

        SortBean sortBean = (SortBean) result;
        assertThat(sortBean.getOrderBy()).isEqualTo("myFieldName");
        assertThat(sortBean.isOrderAsc()).isEqualTo(true);

        camelContext.stop();
    }

    @Test
    public void sortBean2_GivenHeaderWithDesc_ShouldAddSortHeader() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("sort_by", "myFieldName:desc");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-sortBean2");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-sortBean2", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        assertThat(routeExchange.getIn().getBody()).isEqualTo(routeExchange.getOut().getBody());
        assertThat(routeExchange.getOut().getHeaders().entrySet()).containsAll(headers.entrySet());

        Object result = routeExchange.getOut().getHeaders().get(ToBeanRouter.SORT_HEADER_NAME);
        assertThat(result).isInstanceOf(SortBean.class);

        SortBean sortBean = (SortBean) result;
        assertThat(sortBean.getOrderBy()).isEqualTo("myFieldName");
        assertThat(sortBean.isOrderAsc()).isEqualTo(false);

        camelContext.stop();
    }
}

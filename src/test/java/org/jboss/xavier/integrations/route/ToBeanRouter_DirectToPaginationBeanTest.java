package org.jboss.xavier.integrations.route;

import org.apache.camel.Exchange;
import org.jboss.xavier.integrations.route.model.PageBean;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class ToBeanRouter_DirectToPaginationBeanTest extends XavierCamelTest {

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenNoHeaders_ShouldUseDefaultOffsetAndMaxLimit() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-pageBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-pageBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object paginationHeader = routeExchange.getIn().getHeaders().get(ToBeanRouter.PAGE_HEADER_NAME);
        assertThat(paginationHeader).isNotNull();
        assertThat(paginationHeader).isInstanceOf(PageBean.class);

        PageBean pageBean = (PageBean) paginationHeader;
        assertThat(pageBean.getOffset()).isEqualTo(ToBeanRouter.DEFAULT_OFFSET);
        assertThat(pageBean.getLimit()).isEqualTo(ToBeanRouter.MAX_LIMIT);

        camelContext.stop();
    }

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenHeaders_ShouldAddPaginationHeader() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("offset", 2);
        headers.put("limit", 20);
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-pageBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-pageBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object paginationHeader = routeExchange.getIn().getHeaders().get(ToBeanRouter.PAGE_HEADER_NAME);
        assertThat(paginationHeader).isInstanceOf(PageBean.class);

        PageBean pageBean = (PageBean) paginationHeader;
        assertThat(pageBean.getOffset()).isEqualTo(headers.get("offset"));
        assertThat(pageBean.getLimit()).isEqualTo(headers.get("limit"));

        camelContext.stop();
    }

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenNullHeaders_ShouldUseDefaultOffsetAndMaxLimit() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("offset", null);
        headers.put("limit", null);
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-pageBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-pageBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object paginationHeader = routeExchange.getIn().getHeaders().get(ToBeanRouter.PAGE_HEADER_NAME);
        assertThat(paginationHeader).isNotNull();
        assertThat(paginationHeader).isInstanceOf(PageBean.class);

        PageBean pageBean = (PageBean) paginationHeader;
        assertThat(pageBean.getOffset()).isEqualTo(ToBeanRouter.DEFAULT_OFFSET);
        assertThat(pageBean.getLimit()).isEqualTo(ToBeanRouter.MAX_LIMIT);

        camelContext.stop();
    }

    @Test
    public void ToBeanRouterBuilder_routeToPaginationBean_GivenStringHeaders_ShouldUseDefaultOffsetAndMaxLimit() throws Exception {
        //Given
        Map<String, Object> headers = new HashMap<>();
        headers.put("offset", "2");
        headers.put("limit", "20");
        headers.put("anotherHeader", "my custom header value");

        //When
        camelContext.start();
        camelContext.startRoute("to-pageBean");
        Exchange routeExchange = camelContext.createProducerTemplate().request("direct:to-pageBean", exchange -> {
            exchange.getIn().setBody("my custom body");
            exchange.getIn().setHeaders(headers);
        });

        //Then
        Object paginationHeader = routeExchange.getIn().getHeaders().get(ToBeanRouter.PAGE_HEADER_NAME);
        assertThat(paginationHeader).isNotNull();
        assertThat(paginationHeader).isInstanceOf(PageBean.class);

        PageBean pageBean = (PageBean) paginationHeader;
        assertThat(pageBean.getOffset()).isEqualTo(2);
        assertThat(pageBean.getLimit()).isEqualTo(20);

        camelContext.stop();
    }
}

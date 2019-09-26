package org.jboss.xavier.integrations.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.processor.aggregate.zipfile.ZipAggregationStrategy;
import org.jboss.xavier.analytics.pojo.AdministrationMetricsMapper;
import org.jboss.xavier.analytics.pojo.AdministrationMetricsModel;
import org.jboss.xavier.integrations.jpa.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class AdministrationRestRouter extends RouteBuilder {

    @Autowired
    AnalysisService analysisService;

    @Autowired
    AdministrationMetricsMapper administrationMetricsMapper;

    final static String CSC_CORRELATION_ID_HEADER = "CsvCorrelationID";
    final static String WORKING_FOLDER = "tmpFolder";
    final static String DATE_FORMAT = "yyyyMMddHHmmssz";

    @Override
    public void configure() throws Exception {

        /*
         * Generates a zip with 2 files. The first file contains metrics between last MONDAY and THE MONDAY before it
         * using 00:00:00 time. The second file contains all metrics until NOW.
         * NOTE: MONDAY at 00:00:00 is expressed in the Time zone where the application is running.
         * E.g. If the server is running in UTC+2 then the date will be: MONDAY 00:00:00 UTC+2
         */
        from("rest:get:/administration/report/csv?produces=application/octet-stream")
                .id("administration-report-csv")

                .setHeader(CSC_CORRELATION_ID_HEADER, () -> UUID.randomUUID().toString())
                .setHeader("unixEpochDate", () -> new Date(0L))
                .setHeader("nowDate", Date::new)

                // Set fromDate and toDate headers last MONDAY-to-MONDAY
                .process(exchange -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    // Get last MONDAY
                    int dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    calendar.add(Calendar.DAY_OF_WEEK, Calendar.MONDAY - dayOfTheWeek);
                    Date toDate = calendar.getTime();

                    // Reduce 7 days
                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                    Date fromDate = calendar.getTime();

                    // create pagination header
                    Map<String, Object> headers = exchange.getIn().getHeaders();
                    headers.put("fromDate", fromDate);
                    headers.put("toDate", toDate);

                    // store the reply from the bean on the OUT message
                    exchange.getOut().setHeaders(headers);
                    exchange.getOut().setBody(exchange.getIn().getBody());
                    exchange.getOut().setAttachments(exchange.getIn().getAttachments());
                })
                .log("Creating csv first file from date ${date:header.fromDate} to ${date:header.toDate}")
                .bean(analysisService, "getAdministrationMetrics(${header.fromDate}, ${header.toDate})")
                .bean(administrationMetricsMapper, "toAdministrationMetricsModels")
                .to("direct:administrationMetricsModelToCsv")
                .setHeader(Exchange.FILE_NAME, simple("${date:header.fromDate:" + DATE_FORMAT + "}-to-${date:header.toDate:" + DATE_FORMAT +"}.csv"))
                .to("direct:administration-report-csv-aggregator")

                .log("Creating csv second file from date ${header.unixEpochDate} to ${header.nowDate}")
                .bean(analysisService, "getAdministrationMetrics(${header.unixEpochDate}, ${header.nowDate})")
                .bean(administrationMetricsMapper, "toAdministrationMetricsModels")
                .to("direct:administrationMetricsModelToCsv")
                .setHeader(Exchange.FILE_NAME, simple("all.csv"))
                .to("direct:administration-report-csv-aggregator")

                .pollEnrich().simple("file:" + WORKING_FOLDER + "?fileName=${header." + CSC_CORRELATION_ID_HEADER + "}.zip")
                .setHeader("Content-Disposition", simple("attachment;filename=${date:now:" + DATE_FORMAT + "}.zip"));

        from("direct:administrationMetricsModelToCsv")
                .id("administration-metrics-model-to-csv")
                .marshal()
                .bindy(BindyType.Csv, AdministrationMetricsModel.class);

        from("direct:administration-report-csv-aggregator")
                .id("administration-report-csv-aggregator")
                .aggregate(header(CSC_CORRELATION_ID_HEADER), new ZipAggregationStrategy(false, true))
                .completionSize(2)
                .setHeader(Exchange.FILE_NAME, simple("${header." + CSC_CORRELATION_ID_HEADER + "}.zip"))
                .to("file:" + WORKING_FOLDER);
    }

}

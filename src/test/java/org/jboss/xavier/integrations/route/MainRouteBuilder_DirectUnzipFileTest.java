package org.jboss.xavier.integrations.route;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.camel.test.spring.UseAdviceWith;
import org.jboss.xavier.Application;
import org.jboss.xavier.analytics.pojo.output.AnalysisModel;
import org.jboss.xavier.integrations.jpa.service.AnalysisService;
import org.jboss.xavier.integrations.migrationanalytics.business.Calculator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("direct:store|direct:calculate-workloadsummaryreportmodel")
@UseAdviceWith // Disables automatic start of Camel context
@SpringBootTest(classes = {Application.class})
@ActiveProfiles("test")
public class MainRouteBuilder_DirectUnzipFileTest {
    @Autowired
    CamelContext camelContext;

    @EndpointInject(uri = "mock:direct:store")
    private MockEndpoint mockStore;

    @EndpointInject(uri = "mock:direct:calculate-workloadsummaryreportmodel")
    private MockEndpoint mockCalculateWorkloadSummaryReportModel;

    @Value("#{'${insights.properties}'.split(',')}")
    List<String> properties;

    @Inject
    MainRouteBuilder mainRouteBuilder;

    @Inject
    AnalysisService analysisService;

    @Test
    public void mainRouteBuilder_routeDirectUnzip_ZipFileWith3FilesGiven_ShouldReturn3Messages() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);
       // mockCalculate.expectedMessageCount(3);
        mockCalculateWorkloadSummaryReportModel.expectedMessageCount(1);

        //When
        camelContext.start();
        camelContext.startRoute("unzip-file");

        String nameOfFile = "txt-files-samples.zip";
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(nameOfFile);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/zip");
        headers.put(Exchange.FILE_NAME, nameOfFile);

        Map<String,Object> metadata = new HashMap<>();
        metadata.put("filename", nameOfFile);
        metadata.put("dummy", "CID123");
        headers.put("MA_metadata", metadata);


        camelContext.createProducerTemplate().sendBodyAndHeaders("direct:unzip-file", resourceAsStream, headers);

        //Then
        //mockCalculate.assertIsSatisfied();
        mockCalculateWorkloadSummaryReportModel.assertIsSatisfied();

        camelContext.stop();
    }

    @Test
    public void mainRouteBuilder_routeDirectUnzip_TarGzFileWith2FilesGiven_ShouldReturn2Messages() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);
        //mockCalculate.expectedMessageCount(2);
        mockCalculateWorkloadSummaryReportModel.expectedMessageCount(1);

        //When
        camelContext.start();
        camelContext.startRoute("unzip-file");

        String nameOfFile = "cloudforms-export-v1-multiple-files.tar.gz";
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(nameOfFile);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/gzip");
        headers.put(Exchange.FILE_NAME, nameOfFile);

        Map<String,Object> metadata = new HashMap<>();
        metadata.put("filename", nameOfFile);
        metadata.put("dummy", "CID123");
        headers.put("MA_metadata", metadata);

        camelContext.createProducerTemplate().sendBodyAndHeaders("direct:unzip-file", resourceAsStream, headers);

        //Then
        //mockCalculate.assertIsSatisfied();
        mockCalculateWorkloadSummaryReportModel.assertIsSatisfied();

        camelContext.stop();
    }

    @Test
    public void mainRouteBuilder_routeDirectUnzip_TarGzFileWith2FilesGivenAndTarGzContentType_ShouldReturn2Messages() throws Exception {
        AnalysisModel analysisModel = analysisService.buildAndSave("report name", "report desc", "file name", "user name");

        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);
        //mockCalculate.expectedMessageCount(2);
        mockCalculateWorkloadSummaryReportModel.expectedMessageCount(1);

        String nameOfFile = "cloudforms-export-v1-multiple-files.tar.gz";

        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/tar+gz");
        headers.put(Exchange.FILE_NAME, nameOfFile);

        Map<String,Object> metadata = new HashMap<>();
        metadata.put("filename", nameOfFile);
        metadata.put("dummy", "CID123");
        metadata.put(Calculator.YEAR_1_HYPERVISORPERCENTAGE, 10D);
        metadata.put(Calculator.YEAR_2_HYPERVISORPERCENTAGE, 20D);
        metadata.put(Calculator.YEAR_3_HYPERVISORPERCENTAGE, 30D);
        metadata.put(Calculator.GROWTHRATEPERCENTAGE, 7D);
        metadata.put(MainRouteBuilder.ANALYSIS_ID, analysisModel.getId());
        headers.put("MA_metadata", metadata);

        //When
        camelContext.start();
        camelContext.startRoute("unzip-file");
        camelContext.startRoute("calculate");
        camelContext.startRoute("calculate-costsavings");
        camelContext.startRoute("send-costsavings");
        camelContext.startRoute("calculate-vmworkloadinventory");
        camelContext.startRoute("flags-shared-disks");


        //camelContext.createProducerTemplate().sendBodyAndHeaders("direct:unzip-file", resourceAsStream, headers);
        camelContext.createProducerTemplate().request("direct:unzip-file", exchange -> {
            exchange.getIn().setBody(getClass().getClassLoader().getResourceAsStream(nameOfFile));
            exchange.getIn().setHeaders(headers);
        });

        Thread.sleep(5000);
        //Then
        //mockCalculate.assertIsSatisfied();
        mockCalculateWorkloadSummaryReportModel.assertIsSatisfied();

        camelContext.stop();
    }

    @Test
    public void mainRouteBuilder_routeDirectUnzip_JsonFileGiven_ShouldReturn1Messages() throws Exception {
        //Given
        camelContext.setTracing(true);
        camelContext.setAutoStartup(false);
        //mockCalculate.expectedMessageCount(1);
        mockCalculateWorkloadSummaryReportModel.expectedMessageCount(1);

        //When
        camelContext.start();
        camelContext.startRoute("unzip-file");

        String nameOfFile = "cloudforms-export-v1-json";
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(nameOfFile);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        headers.put(Exchange.FILE_NAME, nameOfFile);

        Map<String,Object> metadata = new HashMap<>();
        metadata.put("filename", nameOfFile);
        metadata.put("dummy", "CID123");
        headers.put("MA_metadata", metadata);

        camelContext.createProducerTemplate().sendBodyAndHeaders("direct:unzip-file", resourceAsStream, headers);

        //Then
        //mockCalculate.assertIsSatisfied();
        mockCalculateWorkloadSummaryReportModel.assertIsSatisfied();

        camelContext.stop();
    }




}

package org.jboss.xavier.integrations.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Attachment;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.zipfile.ZipSplitter;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.jboss.xavier.integrations.route.dataformat.CustomizedMultipartDataFormat;
import org.jboss.xavier.integrations.route.model.RHIdentity;
import org.jboss.xavier.integrations.route.model.cloudforms.CloudFormAnalysis;
import org.jboss.xavier.integrations.route.model.notification.FilePersistedNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A Camel Java8 DSL Router
 */
@Component
public class MainRouteBuilder extends RouteBuilder {

    @Value("${insights.upload.host}")
    private String uploadHost;

    @Value("${insights.kafka.host}")
    private String kafkaHost;

    @Value("${insights.upload.mimetype}")
    private String mimeType;

    @Value("${insights.upload.accountnumber}")
    private String accountNumber;

    public void configure() {
        getContext().setTracing(true);

        from("rest:post:/upload/{customerID}?consumes=multipart/form-data")
                .id("rest-upload")
                .to("direct:upload");

        from("direct:upload")
                .id("direct-upload")
                .unmarshal(new CustomizedMultipartDataFormat())
                .split()
                    .attachments()
                    .process(processMultipart())
                    .choice()
                        .when(isZippedFile())
                            .split(new ZipSplitter())
                            .streaming()
                            .to("direct:store")
                        .endChoice()
                        .otherwise()
                            .to("direct:store");

        from("direct:store")
                .id("direct-store")
                .convertBodyTo(String.class)
                .to("file:./upload")
                .to("direct:insights");

        from("direct:insights")
                .id("call-insights-upload-service")
                .process(this::createMultipartToSendToInsights)
                .setHeader("x-rh-identity", method(MainRouteBuilder.class, "getRHIdentity(${header.customerid}, ${header.CamelFileName})"))
                .setHeader("x-rh-insights-request-id", method(MainRouteBuilder.class, "getRHInsightsRequestId()"))
                .removeHeaders("Camel*")
                .setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.POST))
                .to("http4://" + uploadHost + "/api/ingress/v1/upload")
                .to("log:INFO?showBody=true&showHeaders=true")
                .end();

        from("kafka:" + kafkaHost + "?topic={{insights.kafka.upload.topic}}&brokers=" + kafkaHost + "&autoOffsetReset=latest&autoCommitEnable=true")
                .id("kafka-upload-message")
                .unmarshal().json(JsonLibrary.Jackson, FilePersistedNotification.class)
                .filter(simple("'{{insights.service}}' == ${body.getService}"))
                .to("direct:download-file");

        from("direct:download-file")
                .id("download-file")
                .setHeader("Exchange.HTTP_URI", simple("${body.url}"))
                .process( exchange -> {
                    FilePersistedNotification filePersistedNotification = exchange.getIn().getBody(FilePersistedNotification.class);
                    String identity_json = new String(Base64.getDecoder().decode(filePersistedNotification.getB64_identity()));
                    RHIdentity rhIdentity = new ObjectMapper().reader().forType(RHIdentity.class).withRootName("identity").readValue(identity_json);
                    exchange.getIn().setHeader("customerid", rhIdentity.getInternal().get("customerid"));
                    exchange.getIn().setHeader("filename", rhIdentity.getInternal().get("filename"));
                })
                .setBody(constant(""))
                .to("http4://oldhost")
                .removeHeader("Exchange.HTTP_URI")
                .convertBodyTo(String.class)
                .to("direct:calculate");

        from("direct:calculate")
                .id("calculate")
                .unmarshal().json(JsonLibrary.Jackson, CloudFormAnalysis.class)
                .transform().method("analyticsCalculator", "calculate(${body}, ${header.customerid}, ${header.filename})")
                .log("Message to send to AMQ : ${body}")
                .to("jms:queue:inputDataModel");
    }

    private void createMultipartToSendToInsights(Exchange exchange) {
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntityBuilder.setContentType(ContentType.MULTIPART_FORM_DATA);

        String file = exchange.getIn().getBody(String.class);
        multipartEntityBuilder.addPart("upload", new ByteArrayBody(file.getBytes(), ContentType.create(mimeType), exchange.getIn().getHeader(Exchange.FILE_NAME, String.class)));
        exchange.getIn().setBody(multipartEntityBuilder.build());
    }

    public String getRHInsightsRequestId() {
        // 52df9f748eabcfea
        return UUID.randomUUID().toString();
    }

    public String getRHIdentity(String customerid, String filename) {
        Map<String,String> internal = new HashMap<>();
        internal.put("customerid", customerid);
        internal.put("filename", filename);
        internal.put("org_id", "000001");
        String rhIdentity_json = "";
        try {
            rhIdentity_json = new ObjectMapper().writer().withRootName("identity").writeValueAsString(RHIdentity.builder()
                    .account_number(accountNumber)
                    .internal(internal)
                    .build());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(rhIdentity_json.getBytes());
    }

    private Predicate isZippedFile() {
        return exchange -> "application/zip".equalsIgnoreCase(exchange.getMessage().getHeader("part_contenttype").toString());
    }

    private Processor processMultipart() {
        return exchange -> {
            DataHandler dataHandler = exchange.getIn().getBody(Attachment.class).getDataHandler();
            exchange.getIn().setHeader(Exchange.FILE_NAME, dataHandler.getName());
            exchange.getIn().setHeader("part_contenttype", dataHandler.getContentType());
            exchange.getIn().setHeader("part_name", dataHandler.getName());
            exchange.getIn().setBody(dataHandler.getInputStream());
        };
    }


}

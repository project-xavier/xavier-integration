package org.jboss.xavier.integrations.route;

import org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RHIdentityTest
{
    @Test
    public void test()
    {
        try {
            String customerId = "CID123";
            String fileName = "cloudforms-export-v1.json";
            Integer hypervisor = 1;
            Long totaldiskspace = 281951062016L;
            Integer sourceproductindicator = 1;
            Double year1hypervisorpercentage = 10D;
            Double year2hypervisorpercentage = 20D;
            Double year3hypervisorpercentage = 30D;
            Double growthratepercentage = 7D;

            UploadFormInputDataModel expectedFormInputDataModelExpected = new UploadFormInputDataModel(customerId, fileName, hypervisor, totaldiskspace, sourceproductindicator, year1hypervisorpercentage, year2hypervisorpercentage, year3hypervisorpercentage, growthratepercentage);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("customerid", customerId);
            metadata.put("filename", fileName);
            metadata.put("year1hypervisorpercentage", year1hypervisorpercentage);
            metadata.put("year2hypervisorpercentage", year2hypervisorpercentage);
            metadata.put("year3hypervisorpercentage", year3hypervisorpercentage);
            metadata.put("growthratepercentage", growthratepercentage);
            metadata.put("sourceproductindicator", sourceproductindicator);

            Map<String, Object> headers = new HashMap<>();
            headers.put("MA_metadata", metadata);

            MainRouteBuilder mainRouteBuilder = new MainRouteBuilder();
            String result = mainRouteBuilder.getRHIdentity("eyJlbnRpdGxlbWVudHMiOnsiaW5zaWdodHMiOnsiaXNfZW50aXRsZWQiOnRydWV9LCJvcGVuc2hpZnQiOnsiaXNfZW50aXRsZWQiOnRydWV9LCJzbWFydF9tYW5hZ2VtZW50Ijp7ImlzX2VudGl0bGVkIjpmYWxzZX0sImh5YnJpZF9jbG91ZCI6eyJpc19lbnRpdGxlZCI6dHJ1ZX19LCJpZGVudGl0eSI6eyJpbnRlcm5hbCI6eyJhdXRoX3RpbWUiOjAsImF1dGhfdHlwZSI6Imp3dC1hdXRoIiwib3JnX2lkIjoiNjM0MDA1NiJ9LCJhY2NvdW50X251bWJlciI6IjE0NjAyOTAiLCJ1c2VyIjp7ImZpcnN0X25hbWUiOiJNYXJjbyIsImlzX2FjdGl2ZSI6dHJ1ZSwiaXNfaW50ZXJuYWwiOnRydWUsImxhc3RfbmFtZSI6IlJpenppIiwibG9jYWxlIjoiZW5fVVMiLCJpc19vcmdfYWRtaW4iOmZhbHNlLCJ1c2VybmFtZSI6Im1yaXp6aUByZWRoYXQuY29tIiwiZW1haWwiOiJtcml6emkrcWFAcmVkaGF0LmNvbSJ9LCJ0eXBlIjoiVXNlciJ9fQ==",
                    "fileName.json", headers);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

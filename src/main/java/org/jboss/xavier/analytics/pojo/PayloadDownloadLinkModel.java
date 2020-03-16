package org.jboss.xavier.analytics.pojo;

public class PayloadDownloadLinkModel {

    private String filename;
    private String downloadLink;

    public PayloadDownloadLinkModel() {
    }

    public PayloadDownloadLinkModel(String filename, String downloadLink) {
        this.filename = filename;
        this.downloadLink = downloadLink;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}

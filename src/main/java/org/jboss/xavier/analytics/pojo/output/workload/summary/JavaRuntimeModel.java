package org.jboss.xavier.analytics.pojo.output.workload.summary;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class JavaRuntimeModel {

    @JsonIgnore
    @EmbeddedId
    private JavaRuntimeIdentityModel id;

    // This comes from FlagAssessmentIdentityModel
    @Column(insertable = false, updatable = false)
    private String vendor;

    // This comes from FlagAssessmentIdentityModel
    @Column(insertable = false, updatable = false)
    private String version;

    private String workload;

    public JavaRuntimeIdentityModel getId() {
        return id;
    }

    public void setId(JavaRuntimeIdentityModel id) {
        this.id = id;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String flag) {
        this.vendor = flag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String osName) {
        this.version = osName;
    }

    public String getWorkload() {
        return workload;
    }

    public void setWorkload(String workload) {
        this.workload = workload;
    }

}

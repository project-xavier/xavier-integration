package org.jboss.xavier.analytics.pojo.output.workload.summary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class FlagAssessmentOSModel implements java.io.Serializable {

    @JsonIgnore
    @EmbeddedId
    private FlagAssessmentOSIdentityModel id;

    // This comes from FlagAssessmentOSIdentityModel
    @Column(insertable = false, updatable = false)
    private String osName;

    // This comes from FlagAssessmentOSIdentityModel
    @Column(insertable = false, updatable = false)
    private String assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flag_assessment_id")
    @JsonBackReference
    private FlagAssessmentModel flagAssessmentModel;

    public FlagAssessmentOSIdentityModel getId() {
        return id;
    }

    public void setId(FlagAssessmentOSIdentityModel id) {
        this.id = id;
    }

    public FlagAssessmentModel getFlagAssessmentModel() {
        return flagAssessmentModel;
    }

    public void setFlagAssessmentModel(FlagAssessmentModel flagAssessmentModel) {
        this.flagAssessmentModel = flagAssessmentModel;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }
}

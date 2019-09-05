package org.jboss.xavier.analytics.pojo.output.workload.summary;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class FlagAssessmentOSModel implements java.io.Serializable {

    @EmbeddedId
    private FlagAssessmentOSIdentityModel id;

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
}

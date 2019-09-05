package org.jboss.xavier.analytics.pojo.output.workload.summary;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class FlagAssessmentModel implements java.io.Serializable {

    @Id
    private String flag;
    private String label;

    @OneToMany(mappedBy = "flagAssessmentModel", fetch = FetchType.EAGER)
    private Set<FlagAssessmentOSModel> flagAssessmentOSModels;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Set<FlagAssessmentOSModel> getFlagAssessmentOSModels() {
        return flagAssessmentOSModels;
    }

    public void setFlagAssessmentOSModels(Set<FlagAssessmentOSModel> flagAssessmentOSModels) {
        this.flagAssessmentOSModels = flagAssessmentOSModels;
    }
}

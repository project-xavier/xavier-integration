package org.jboss.xavier.analytics.pojo.output.workload.summary;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class FlagAssessmentOSIdentityModel implements java.io.Serializable {

    private String osName;
    private String assessment;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlagAssessmentOSIdentityModel that = (FlagAssessmentOSIdentityModel) o;
        return Objects.equals(osName, that.osName) &&
                assessment.equals(that.assessment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(osName, assessment);
    }
}

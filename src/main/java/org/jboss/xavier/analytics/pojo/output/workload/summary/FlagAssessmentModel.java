package org.jboss.xavier.analytics.pojo.output.workload.summary;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FlagAssessmentModel
{
    @Id
    private Long id;
    private String flag;
    private String assessment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }
}

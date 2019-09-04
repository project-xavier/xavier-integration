package org.jboss.xavier.analytics.pojo.output.workload.summary;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "FlagAssessmentModel_" +
                FlagAssessmentModel.FLAG + "_unique",
                columnNames = FlagAssessmentModel.FLAG)
})
public class FlagAssessmentModel implements java.io.Serializable {

    static final String FLAG = "flag";

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

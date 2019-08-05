package org.jboss.xavier.analytics.pojo.input;

public abstract class AbstractInputModel
{
    private Long analysisId;

    public AbstractInputModel() {}

    public AbstractInputModel(Long analysisId)
    {
        this.analysisId = analysisId;
    }

    public Long getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Long analysisId) {
        this.analysisId = analysisId;
    }
}

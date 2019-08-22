package org.jboss.xavier.analytics.pojo.output.workload.summary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.jboss.xavier.analytics.pojo.output.AnalysisModel;
import org.springframework.stereotype.Component;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.transaction.Transactional;
import java.beans.Transient;
import java.util.List;

@Entity
@Transactional
@Table(
        indexes = {
                @Index(name = "WorkloadSummaryReportModel_" +
                        WorkloadSummaryReportModel.ANALYSIS_ID + "_index",
                        columnList = WorkloadSummaryReportModel.ANALYSIS_ID, unique = false)
        }
)
@Component
public class WorkloadSummaryReportModel
{
    static final long serialVersionUID = 1L;
    static final String ANALYSIS_ID = "analysis_id";

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.AUTO, generator = "WORKLOADSUMMARYREPORTMODEL_ID_GENERATOR")
    @GenericGenerator(
            name = "WORKLOADSUMMARYREPORTMODEL_ID_GENERATOR",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "WORKLOADSUMMARYREPORT_SEQUENCE")
            }
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ANALYSIS_ID)
    @JsonBackReference
    private AnalysisModel analysis;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SummaryModel> summaryModels;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private ComplexityModel complexityModel;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private RecommendedTargetsIMSModel recommendedTargetsIMSModel;

    // Models are still no defined
    transient WorkloadsDetected workloadsDetected;
    transient List<ScanRunModel> scanRuns;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkloadModel> workloadModels;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FlagModel> flagModels;

    public WorkloadSummaryReportModel() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnalysisModel getAnalysis() {
        return analysis;
    }

    public void setAnalysis(AnalysisModel analysis) {
        this.analysis = analysis;
    }

    public List<SummaryModel> getSummaryModels() {
        return summaryModels;
    }

    public void setSummaryModels(List<SummaryModel> summaryModels) {
        summaryModels.forEach(model -> model.setReport(this));
        this.summaryModels = summaryModels;
    }

    public ComplexityModel getComplexityModel() {
        return complexityModel;
    }

    public void setComplexityModel(ComplexityModel complexityModel) {
        this.complexityModel = complexityModel;
    }

    public RecommendedTargetsIMSModel getRecommendedTargetsIMSModel() {
        return recommendedTargetsIMSModel;
    }

    public void setRecommendedTargetsIMSModel(RecommendedTargetsIMSModel recommendedTargetsIMSModel) {
        this.recommendedTargetsIMSModel = recommendedTargetsIMSModel;
    }

    public List<WorkloadModel> getWorkloadModels() {
        return workloadModels;
    }

    public List<FlagModel> getFlagModels() {
        return flagModels;
    }

    public void setFlagModels(List<FlagModel> flagModels) {
        this.flagModels = flagModels;
    }

    public void setWorkloadModels(List<WorkloadModel> workloadModels) {
        this.workloadModels = workloadModels;
    }

    public WorkloadsDetected getWorkloadsDetected() {
        return workloadsDetected;
    }

    public void setWorkloadsDetected(WorkloadsDetected workloadsDetected) {
        this.workloadsDetected = workloadsDetected;
    }

    public List<ScanRunModel> getScanRuns() {
        return scanRuns;
    }

    public void setScanRuns(List<ScanRunModel> scanRuns) {
        this.scanRuns = scanRuns;
    }
}

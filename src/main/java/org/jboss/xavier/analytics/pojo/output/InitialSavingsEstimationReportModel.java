package org.jboss.xavier.analytics.pojo.output;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kie.api.definition.type.Label;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Date;

/**
 * This class was automatically generated by the data modeler tool.
 */

@Entity
public class InitialSavingsEstimationReportModel
        implements
        java.io.Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.AUTO, generator = "INITIALSAVINGSESTIMATIONREPORTMODEL_ID_GENERATOR")
    @GenericGenerator(
            name = "INITIALSAVINGSESTIMATIONREPORTMODEL_ID_GENERATOR",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "INITIALSAVINGSESTIMATIONREPORT_SEQUENCE")
            }
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id")
    @JsonBackReference
    private AnalysisModel analysis;

    @Label("Customer ID")
    private String customerId;

    @Label("Source payload file name")
    private String fileName;

    @Label(value = "Date of creation")
    private Date creationDate;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private EnvironmentModel environmentModel;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private SourceCostsModel sourceCostsModel;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private SourceRampDownCostsModel sourceRampDownCostsModel;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private RHVRampUpCostsModel rhvRampUpCostsModel;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private RHVYearByYearCostsModel rhvYearByYearCostsModel;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private RHVSavingsModel rhvSavingsModel;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private RHVAdditionalContainerCapacityModel rhvAdditionalContainerCapacityModel;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private RHVOrderFormModel rhvOrderFormModel;

    public InitialSavingsEstimationReportModel() {}

    public InitialSavingsEstimationReportModel(String customerId, String fileName, Date creationDate, EnvironmentModel environmentModel, SourceCostsModel sourceCostsModel, SourceRampDownCostsModel sourceRampDownCostsModel, RHVRampUpCostsModel rhvRampUpCostsModel, RHVYearByYearCostsModel rhvYearByYearCostsModel, RHVSavingsModel rhvSavingsModel, RHVAdditionalContainerCapacityModel rhvAdditionalContainerCapacityModel, RHVOrderFormModel rhvOrderFormModel) {
        this.customerId = customerId;
        this.fileName = fileName;
        this.creationDate = creationDate;
        this.environmentModel = environmentModel;
        this.sourceCostsModel = sourceCostsModel;
        this.sourceRampDownCostsModel = sourceRampDownCostsModel;
        this.rhvRampUpCostsModel = rhvRampUpCostsModel;
        this.rhvYearByYearCostsModel = rhvYearByYearCostsModel;
        this.rhvSavingsModel = rhvSavingsModel;
        this.rhvAdditionalContainerCapacityModel = rhvAdditionalContainerCapacityModel;
        this.rhvOrderFormModel = rhvOrderFormModel;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public EnvironmentModel getEnvironmentModel() {
        return environmentModel;
    }

    public void setEnvironmentModel(EnvironmentModel environmentModel) {
        this.environmentModel = environmentModel;
    }

    public SourceCostsModel getSourceCostsModel() {
        return sourceCostsModel;
    }

    public void setSourceCostsModel(SourceCostsModel sourceCostsModel) {
        this.sourceCostsModel = sourceCostsModel;
    }

    public SourceRampDownCostsModel getSourceRampDownCostsModel() {
        return sourceRampDownCostsModel;
    }

    public void setSourceRampDownCostsModel(SourceRampDownCostsModel sourceRampDownCostsModel) {
        this.sourceRampDownCostsModel = sourceRampDownCostsModel;
    }

    public RHVRampUpCostsModel getRhvRampUpCostsModel() {
        return rhvRampUpCostsModel;
    }

    public void setRhvRampUpCostsModel(RHVRampUpCostsModel rhvRampUpCostsModel) {
        this.rhvRampUpCostsModel = rhvRampUpCostsModel;
    }

    public RHVYearByYearCostsModel getRhvYearByYearCostsModel() {
        return rhvYearByYearCostsModel;
    }

    public void setRhvYearByYearCostsModel(RHVYearByYearCostsModel rhvYearByYearCostsModel) {
        this.rhvYearByYearCostsModel = rhvYearByYearCostsModel;
    }

    public RHVSavingsModel getRhvSavingsModel() {
        return rhvSavingsModel;
    }

    public void setRhvSavingsModel(RHVSavingsModel rhvSavingsModel) {
        this.rhvSavingsModel = rhvSavingsModel;
    }

    public RHVAdditionalContainerCapacityModel getRhvAdditionalContainerCapacityModel() {
        return rhvAdditionalContainerCapacityModel;
    }

    public void setRhvAdditionalContainerCapacityModel(RHVAdditionalContainerCapacityModel rhvAdditionalContainerCapacityModel) {
        this.rhvAdditionalContainerCapacityModel = rhvAdditionalContainerCapacityModel;
    }

    public RHVOrderFormModel getRhvOrderFormModel() {
        return rhvOrderFormModel;
    }

    public void setRhvOrderFormModel(RHVOrderFormModel rhvOrderFormModel) {
        this.rhvOrderFormModel = rhvOrderFormModel;
    }

    public AnalysisModel getAnalysis() {
        return analysis;
    }

    public void setAnalysis(AnalysisModel analysis) {
        this.analysis = analysis;
    }
}

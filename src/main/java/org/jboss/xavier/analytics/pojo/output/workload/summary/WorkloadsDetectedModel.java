package org.jboss.xavier.analytics.pojo.output.workload.summary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@SqlResultSetMapping(
        name = "mappingWorkloadsDetectedModels",
        classes = @ConstructorResult(
                targetClass = WorkloadsDetectedModel.class,
                columns = {
                        @ColumnResult(name = "rhel", type = Integer.class),
                        @ColumnResult(name = "sles", type = Integer.class),
                        @ColumnResult(name = "windows", type = Integer.class),
                        @ColumnResult(name = "oel", type = Integer.class)
                }
        )
)

@NamedNativeQuery(
        name = "WorkloadsDetectedModel.calculateWorkloadsDetectedModels",
        query = "select sum(case when lower(complexity)=rhel then 1 else 0 end) as easy, sum(case when lower(complexity)=sles then 1 else 0 end) as medium, sum(case when lower(complexity)=windows then 1 else 0 end) as hard, sum(case when (complexity is null or lower(complexity)=oel) then 1 else 0 end) as \"unknown\" from workload_inventory_report_model where analysis_id = :analysisId",
        resultSetMapping = "mappingWorkloadsDetectedModels"
)

@Entity
public class WorkloadsDetectedModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "WORKLOADSDETECTEDMODEL_ID_GENERATOR")
    @GenericGenerator(
            name = "WORKLOADSDETECTEDMODEL_ID_GENERATOR",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "WORKLOADSDETECTEDMODEL_SEQUENCE")
            }
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JsonBackReference
    private WorkloadSummaryReportModel report;

    private Integer rhel;
    private Integer sles;
    private Integer windows;
    private Integer oel;

    public WorkloadsDetectedModel() {}

    public WorkloadsDetectedModel(Integer rhel, Integer sles, Integer windows, Integer oel) {
        this.rhel = rhel;
        this.sles = sles;
        this.windows = windows;
        this.oel = oel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkloadSummaryReportModel getReport() {
        return report;
    }

    public void setReport(WorkloadSummaryReportModel report) {
        this.report = report;
    }

    public Integer getRhel() {
        return rhel;
    }

    public void setRhel(Integer rhel) {
        this.rhel = rhel;
    }

    public Integer getSles() {
        return sles;
    }

    public void setSles(Integer sles) {
        this.sles = sles;
    }

    public Integer getWindows() {
        return windows;
    }

    public void setWindows(Integer windows) {
        this.windows = windows;
    }

    public Integer getOel() {
        return oel;
    }

    public void setOel(Integer oel) {
        this.oel = oel;
    }

    @Override
    public String toString() {
        return "WorkloadsDetectedModel{" +
                "id=" + id +
                ", report=" + report +
                ", rhel='" + rhel +
                ", sles=" + sles +
                ", windows=" + windows +
                ", oel=" + oel +
                '}';
    }
}

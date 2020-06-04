package org.jboss.xavier.analytics.pojo.output.workload.summary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@SqlResultSetMapping(
        name = "mappingWorkloadsApplicationPlatformsDetectedModels",
        classes = @ConstructorResult(
                targetClass = WorkloadsApplicationPlatformsDetectedModel.class,
                columns = {
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "version", type = String.class),
                        @ColumnResult(name = "priority", type = Integer.class),
                        @ColumnResult(name = "total", type = Integer.class)
                }
        )
)

@NamedNativeQuery(
        name = "WorkloadsApplicationPlatformsDetectedModel.calculateWorkloadApplicationPlatformsDetectedModels",
        query = "select AP.name as name, AP.version as version, AP.priority as priority, count(WIR.id) as total \n" +
                "from analysis_model A \n" +
                "inner join workload_inventory_report_model WIR on WIR.analysis_id = A.id \n" +
                "inner join workload_inventory_report_model_workloads W on W.workload_inventory_report_model_id = WIR.id \n" +
                "inner join app_identifier_model AP on AP.identifier = W.workloads \n" +
                "where AP.group_name='" + WorkloadsApplicationPlatformsDetectedModel.APP_IDENTIFIER + "' and A.id = :analysisId \n" +
                "group by AP.name, AP.version, AP.priority",
        resultSetMapping = "mappingWorkloadsApplicationPlatformsDetectedModels"
)
@Entity
public class WorkloadsApplicationPlatformsDetectedModel {
    public final static String APP_IDENTIFIER = "APPLICATION_PLATFORM";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "WORKLOADSAPPLICATIONPLATFORMSDETECTEDMODEL_ID_GENERATOR")
    @GenericGenerator(
            name = "WORKLOADSAPPLICATIONPLATFORMSDETECTEDMODEL_ID_GENERATOR",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "WORKLOADSAPPLICATIONPLATFORMSDETECTEDMODEL_SEQUENCE")
            }
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    @JsonBackReference
    private WorkloadSummaryReportModel report;

    private String name;
    private String version;
    private Integer priority;
    private Integer total;

    public WorkloadsApplicationPlatformsDetectedModel() {
    }

    public WorkloadsApplicationPlatformsDetectedModel(String name, String version, Integer priority, Integer total) {
        this.name = name;
        this.version = version;
        this.priority = priority;
        this.total = total;
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

    public String getName() {
        return name;
    }

    public void setName(String vendor) {
        this.name = vendor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "WorkloadsApplicationPlatformsDetectedModel{" +
                "id=" + id +
                ", report=" + report +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", priority=" + priority +
                ", total=" + total +
                '}';
    }
}

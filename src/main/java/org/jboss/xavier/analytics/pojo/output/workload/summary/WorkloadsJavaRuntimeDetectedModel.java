package org.jboss.xavier.analytics.pojo.output.workload.summary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@SqlResultSetMapping(
        name = "mappingWorkloadsJavaRuntimeDetectedModels",
        classes = @ConstructorResult(
                targetClass = WorkloadsJavaRuntimeDetectedModel.class,
                columns = {
                        @ColumnResult(name = "vendor", type = String.class),
                        @ColumnResult(name = "version", type = String.class),
                        @ColumnResult(name = "total", type = Integer.class)
                }
        )
)

@NamedNativeQuery(
        name = "WorkloadsJavaRuntimeDetectedModel.calculateWorkloadsJavaRuntimeDetectedModels",
        query = "select JRM.vendor as vendor, JRM.version as version, count(WIR.id) as total \n" +
                "from analysis_model A \n" +
                "inner join workload_inventory_report_model WIR on WIR.analysis_id = A.id \n" +
                "inner join workload_inventory_report_model_workloads W on W.workload_inventory_report_model_id = WIR.id \n" +
                "inner join java_runtime_model JRM on JRM.workload = W.workloads \n" +
                "where A.id = :analysisId \n" +
                "group by W.workloads, JRM.vendor, JRM.version",
        resultSetMapping = "mappingWorkloadsJavaRuntimeDetectedModels"
)
@Entity
public class WorkloadsJavaRuntimeDetectedModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "WORKLOADSJAVARUNTIMEDETECTEDMODEL_ID_GENERATOR")
    @GenericGenerator(
            name = "WORKLOADSJAVARUNTIMEDETECTEDMODEL_ID_GENERATOR",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "WORKLOADSJAVARUNTIMEDETECTEDMODEL_SEQUENCE")
            }
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    @JsonBackReference
    private WorkloadSummaryReportModel report;

    private String vendor;
    private String version;
    private Integer total;

    public WorkloadsJavaRuntimeDetectedModel() {}

    public WorkloadsJavaRuntimeDetectedModel(String vendor, String version, Integer total) {
        this.vendor = vendor;
        this.version = version;
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

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "WorkloadsJavaRuntimeDetectedModel{" +
                "id=" + id +
                ", report=" + report +
                ", vendor='" + vendor + '\'' +
                ", version='" + version + '\'' +
                ", total=" + total +
                '}';
    }
}

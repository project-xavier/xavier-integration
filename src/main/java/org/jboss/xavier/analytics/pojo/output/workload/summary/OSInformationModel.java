package org.jboss.xavier.analytics.pojo.output.workload.summary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@SqlResultSetMapping(
        name = "mappingOSInformationModels",
        classes = @ConstructorResult(
                targetClass = OSInformationModel.class,
                columns = {
                        @ColumnResult(name = "osFamily", type = String.class),
                        @ColumnResult(name = "version", type = String.class),
                        @ColumnResult(name = "priority", type = Integer.class),
                        @ColumnResult(name = "total", type = Integer.class)
                }
        )
)

@NamedNativeQuery(
        name = "OSInformationModel.calculateOSFamiliesModels",
        query = "select AP.name as osFamily, AP.version as version, AP.priority as priority, count(WIR.id) as total \n" +
                "from analysis_model A \n" +
                "inner join workload_inventory_report_model WIR on WIR.analysis_id = A.id \n" +
                "inner join app_identifier_model AP on AP.identifier = WIR.os_family \n" +
                "where AP.group_name='" + OSInformationModel.APP_IDENTIFIER + "' and A.id = :analysisId \n" +
                "group by AP.name, AP.version, AP.priority",
        resultSetMapping = "mappingOSInformationModels"
)
@Entity
public class OSInformationModel {
    public final static String APP_IDENTIFIER = "OS_INFORMATION";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "OSINFORMATIONMODEL_ID_GENERATOR")
    @GenericGenerator(
            name = "OSINFORMATIONMODEL_ID_GENERATOR",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "OSINFORMATIONMODEL_SEQUENCE")
            }
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    @JsonBackReference
    private WorkloadSummaryReportModel report;

    private String osFamily;
    private String version;
    private Integer priority;
    private Integer total;

    public OSInformationModel() {
    }

    public OSInformationModel(String osFamily, String version, Integer priority, Integer total) {
        this.osFamily = osFamily;
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

    public String getOsFamily() {
        return osFamily;
    }

    public void setOsFamily(String osFamily) {
        this.osFamily = osFamily;
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
        return "OSInformationModel{" +
                "id=" + id +
                ", report=" + report +
                ", osFamily='" + osFamily + '\'' +
                ", version='" + version + '\'' +
                ", priority=" + priority +
                ", total=" + total +
                '}';
    }

}

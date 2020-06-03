package org.jboss.xavier.analytics.pojo.output.workload.summary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@SqlResultSetMapping(
        name = "mappingRecommendedTargetsIMSModels",
        classes = @ConstructorResult(
                targetClass = RecommendedTargetsIMSModel.class,
                columns = {
                        @ColumnResult(name = "total", type = Integer.class),
                        @ColumnResult(name = "rhv", type = Integer.class),
                        @ColumnResult(name = "osp", type = Integer.class),
                        @ColumnResult(name = "rhel", type = Integer.class),
                        @ColumnResult(name = "ocp", type = Integer.class),
                        @ColumnResult(name = "openjdk", type = Integer.class),
                        @ColumnResult(name = "jbosseap", type = Integer.class)
                }
        )
)

@NamedNativeQuery(
        name = "RecommendedTargetsIMSModel.calculateRecommendedTargetsIMS",
        query = "select count(distinct wi.id) as total, " +
                "coalesce(sum(case when lower(rt.recommended_targetsims)='rhv' then 1 else 0 end), 0) as rhv, " +
                "coalesce(sum(case when lower(rt.recommended_targetsims)='osp' then 1 else 0 end), 0) as osp, " +
                "coalesce(sum(case when lower(rt.recommended_targetsims)='rhel' then 1 else 0 end), 0) as rhel, " +
                "coalesce(sum(case when lower(rt.recommended_targetsims)='ocp' then 1 else 0 end), 0) as ocp, " +
                "coalesce(sum(case when lower(rt.recommended_targetsims)='openjdk' then 1 else 0 end), 0) as openjdk, " +
                "coalesce(sum(case when lower(rt.recommended_targetsims)='red hat jboss eap' then 1 else 0 end), 0) as jbosseap " +
                "from workload_inventory_report_model_recommended_targetsims rt " +
                "right join workload_inventory_report_model wi on rt.workload_inventory_report_model_id=wi.id " +
                "where wi.analysis_id = :analysisId",
        resultSetMapping = "mappingRecommendedTargetsIMSModels"
)

@Entity
public class RecommendedTargetsIMSModel
{

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.AUTO, generator = "RECOMMENDEDTARGETSIMS_ID_GENERATOR")
    @GenericGenerator(
            name = "RECOMMENDEDTARGETSIMS_ID_GENERATOR",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "RECOMMENDEDTARGETSIMS_SEQUENCE")
            }
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    @JsonBackReference
    private WorkloadSummaryReportModel report;

    private Integer total;
    private Integer rhv;
    private Integer rhel;
    private Integer osp;
    private Integer ocp;
    private Integer openjdk;
    private Integer jbosseap;

    public RecommendedTargetsIMSModel() {}

    public RecommendedTargetsIMSModel(Integer total, Integer rhv, Integer osp, Integer rhel, Integer ocp, Integer openjdk, Integer jbosseap) {
        this.total = total;
        this.rhv = rhv;
        this.osp = osp;
        this.rhel = rhel;
        this.ocp = ocp;
        this.openjdk = openjdk;
        this.jbosseap = jbosseap;
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

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getRhv() {
        return rhv;
    }

    public void setRhv(Integer rhv) {
        this.rhv = rhv;
    }

    public Integer getRhel() {
        return rhel;
    }

    public void setRhel(Integer rhel) {
        this.rhel = rhel;
    }

    public Integer getOsp() {
        return osp;
    }

    public void setOsp(Integer osp) {
        this.osp = osp;
    }

    public Integer getOcp() {
        return ocp;
    }

    public void setOcp(Integer ocp) {
        this.ocp = ocp;
    }

    public Integer getOpenjdk() {
        return openjdk;
    }

    public void setOpenjdk(Integer openjdk) {
        this.openjdk = openjdk;
    }

    public Integer getJbosseap() {
        return jbosseap;
    }

    public void setJbosseap(Integer jbosseap) {
        this.jbosseap = jbosseap;
    }

    @Override
    public String toString() {
        return "RecommendedTargetsIMSModel{" +
                "id=" + id +
                ", report=" + report +
                ", total=" + total +
                ", rhv=" + rhv +
                ", rhel=" + rhel +
                ", osp=" + osp +
                ", ocp=" + ocp +
                ", openjdk=" + openjdk +
                ", jbosseap=" + jbosseap +
                '}';
    }
}

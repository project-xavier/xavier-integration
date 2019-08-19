package org.jboss.xavier.analytics.pojo.output.workload.summary;

public class RecommendedTargetsIMSModel {

    private Long id;


    private Integer total;
    private Integer rhv;
    private Integer rhel;
    private Integer osp;

    public RecommendedTargetsIMSModel() {}

    public RecommendedTargetsIMSModel(Integer total, Integer rhv, Integer osp, Integer rhel) {
        this.total = total;
        this.rhv = rhv;
        this.osp = osp;
        this.rhel = rhel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}

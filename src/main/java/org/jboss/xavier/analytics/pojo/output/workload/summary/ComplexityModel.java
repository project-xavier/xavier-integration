package org.jboss.xavier.analytics.pojo.output.workload.summary;

import java.beans.Transient;


public class ComplexityModel {

    private Long id;


    private Integer easy;
    private Integer medium;
    private Integer difficult;
    private Integer unknown;

    public ComplexityModel() {}

    public ComplexityModel(Integer easy, Integer medium, Integer difficult, Integer unknown) {
        this.easy = easy;
        this.medium = medium;
        this.difficult = difficult;
        this.unknown = unknown;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEasy() {
        return easy;
    }

    public void setEasy(Integer easy) {
        this.easy = easy;
    }

    public Integer getMedium() {
        return medium;
    }

    public void setMedium(Integer medium) {
        this.medium = medium;
    }

    public Integer getDifficult() {
        return difficult;
    }

    public void setDifficult(Integer difficult) {
        this.difficult = difficult;
    }

    public Integer getUnknown() {
        return unknown;
    }

    public void setUnknown(Integer unknown) {
        this.unknown = unknown;
    }
}

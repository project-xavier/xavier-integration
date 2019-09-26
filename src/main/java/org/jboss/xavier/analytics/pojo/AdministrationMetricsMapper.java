package org.jboss.xavier.analytics.pojo;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdministrationMetricsMapper {

    public List<AdministrationMetricsModel> toAdministrationMetricsModels(List<AdministrationMetricsProjection> projections) {
        return projections.stream().map(this::toAdministrationMetricsModel).collect(Collectors.toList());
    }

    public AdministrationMetricsModel toAdministrationMetricsModel(AdministrationMetricsProjection projection) {
        AdministrationMetricsModel model = new AdministrationMetricsModel();
        model.setOwner(projection.getOwner());
        model.setOwnerDomain(projection.getOwnerDomain());
        model.setPayloadName(projection.getPayloadName());
        model.setAnalysisStatus(projection.getAnalysisStatus());
        model.setAnalysisInserted(projection.getAnalysisInserted());
        model.setTotalVms(projection.getTotalVms());
        return model;
    }
}

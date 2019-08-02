package org.jboss.xavier.integrations.jpa.repository;

import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.integrations.route.model.WorkloadInventoryFilterBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class WorkloadInventoryReportSpecs {

    private WorkloadInventoryReportSpecs(){
        // TODO declared private constructor because this should contains just static methods
    }

    public static Specification<WorkloadInventoryReportModel> getByAnalysisIdAndFilterBean(Long analysisId, WorkloadInventoryFilterBean filterBean) {
        List<Specification<WorkloadInventoryReportModel>> specifications = new ArrayList<>();

        // analysisId
        Specification<WorkloadInventoryReportModel> analysisIdSpec = (root, query, cb) -> {
            Join<Object, Object> analysis = root.join("analysis");
            return cb.equal(analysis.get("id"), analysis);
        };

        // filterBean
        if (filterBean.getCluster() != null && !filterBean.getCluster().trim().isEmpty()) {
            Specification<WorkloadInventoryReportModel> clusterSpec = (root, query, cb) -> cb.equal(cb.lower(root.get("cluster")), filterBean.getCluster().toLowerCase());
            specifications.add(clusterSpec);
        }

        if (filterBean.getProvider() != null && !filterBean.getProvider().trim().isEmpty()) {
            Specification<WorkloadInventoryReportModel> providerSpec = (root, query, cb) -> cb.equal(cb.lower(root.get("provider")), filterBean.getProvider().toLowerCase());
            specifications.add(providerSpec);
        }

        if (filterBean.getDatacenter() != null && !filterBean.getDatacenter().trim().isEmpty()) {
            Specification<WorkloadInventoryReportModel> datacenterSpec = (root, query, cb) -> cb.equal(cb.lower(root.get("datacenter")), filterBean.getDatacenter().toLowerCase());
            specifications.add(datacenterSpec);
        }

        if (filterBean.getVmName() != null && !filterBean.getVmName().trim().isEmpty()) {
            Specification<WorkloadInventoryReportModel> vmNameSpec = (root, query, cb) -> cb.like(cb.lower(root.get("vmName")), "%" + filterBean.getVmName().toLowerCase() + "%");
            specifications.add(vmNameSpec);
        }

        if (filterBean.getOsName() != null && !filterBean.getOsName().trim().isEmpty()) {
            Specification<WorkloadInventoryReportModel> osNameSpec = (root, query, cb) -> cb.like(cb.lower(root.get("osName")), "%" + filterBean.getOsName().toLowerCase() + "%");
            specifications.add(osNameSpec);
        }

        if (filterBean.getWorkloads() != null && !filterBean.getWorkloads().isEmpty()) {
            Specification<WorkloadInventoryReportModel> workloadsSpec = (root, query, cb) -> {
                Set<String> workloads = filterBean.getWorkloads();

                List<Predicate> workloadPredicates = new ArrayList<>();
                workloads.forEach(workload -> {
                    Expression<Collection<String>> expression = root.get("workloads");
                    Predicate predicate = cb.isMember(workload.trim(), expression);
                    workloadPredicates.add(predicate);
                });

                return cb.or(workloadPredicates.stream().toArray(Predicate[]::new));
            };

            specifications.add(workloadsSpec);
        }

        if (filterBean.getRecommendedTargetsIMS() != null && !filterBean.getRecommendedTargetsIMS().isEmpty()) {
            Specification<WorkloadInventoryReportModel> recommendedTargetsIMSSpec = (root, query, cb) -> {
                Set<String> recommendedTargetsIMS = filterBean.getRecommendedTargetsIMS();

                List<Predicate> recommendedTargetsIMSPredicates = new ArrayList<>();
                recommendedTargetsIMS.forEach(workload -> {
                    Expression<Collection<String>> expression = root.get("recommendedTargetsIMS");
                    Predicate predicate = cb.isMember(workload, expression);
                    recommendedTargetsIMSPredicates.add(predicate);
                });

                return cb.or(recommendedTargetsIMSPredicates.stream().toArray(Predicate[]::new));
            };

            specifications.add(recommendedTargetsIMSSpec);
        }

        if (filterBean.getFlagsIMS() != null && !filterBean.getFlagsIMS().isEmpty()) {
            Specification<WorkloadInventoryReportModel> flagsIMSSpec = (root, query, cb) -> {
                Set<String> flagsIMS = filterBean.getFlagsIMS();

                List<Predicate> flagsIMSPredicates = new ArrayList<>();
                flagsIMS.forEach(workload -> {
                    Expression<Collection<String>> expression = root.get("flagsIMS");
                    Predicate predicate = cb.isMember(workload, expression);
                    flagsIMSPredicates.add(predicate);
                });

                return cb.or(flagsIMSPredicates.stream().toArray(Predicate[]::new));
            };

            specifications.add(flagsIMSSpec);
        }

        if (filterBean.getComplexity() != null && !filterBean.getComplexity().trim().isEmpty()) {
            Specification<WorkloadInventoryReportModel> complexityIdSpec = (root, query, cb) -> cb.equal(cb.lower(root.get("complexity")), filterBean.getComplexity().toLowerCase());
            specifications.add(complexityIdSpec);
        }

        // union of specifications
        Specifications<WorkloadInventoryReportModel> where = Specifications.where(analysisIdSpec);
        for (Specification<WorkloadInventoryReportModel> specification : specifications) {
            where = where.and(specification);
        }

        return where;
    }
}

package org.jboss.xavier.integrations.jpa.repository;

import org.jboss.xavier.analytics.pojo.output.WorkloadInventoryReportModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface WorkloadInventoryReportRepository extends JpaRepository<WorkloadInventoryReportModel, Long>, JpaSpecificationExecutor<WorkloadInventoryReportModel>
{
    Page<WorkloadInventoryReportModel> findByAnalysisId(Long analysisId, Pageable pageable);

    Page<WorkloadInventoryReportModel> findByAnalysisId(Long analysisId, Specification<WorkloadInventoryReportModel> specification, Pageable pageable);

    @Query(value = "select distinct wir.provider from WorkloadInventoryReportModel wir where wir.analysis.id = :analysisId")
    Set<String> findAllDistinctProvidersByAnalysisId(@Param("analysisId") Long analysisId);

    @Query(value = "select distinct wir.cluster from WorkloadInventoryReportModel wir where wir.analysis.id = :analysisId")
    Set<String> findAllDistinctClustersByAnalysisId(@Param("analysisId") Long analysisId);

    @Query(value = "select distinct wir.datacenter from WorkloadInventoryReportModel wir where wir.analysis.id = :analysisId")
    Set<String> findAllDistinctDatacentersByAnalysisId(@Param("analysisId") Long analysisId);

    @Query(value = "select distinct wir.complexity from WorkloadInventoryReportModel wir where wir.analysis.id = :analysisId")
    Set<String> findAllDistinctComplexitiesByAnalysisId(@Param("analysisId") Long analysisId);

    @Query(value = "select distinct workloads from workload_inventory_report_model_workloads where workload_inventory_report_model_id = :analysisId", nativeQuery = true)
    Set<String> findAllDistinctWorkloadsByAnalysisId(@Param("analysisId") Long analysisId);

    @Query(value = "select distinct recommended_targetsims from workload_inventory_report_model_recommended_targetsims where workload_inventory_report_model_id = :analysisId", nativeQuery = true)
    Set<String> findAllDistinctRecommendedTargetsIMSByAnalysisId(@Param("analysisId") Long analysisId);

    @Query(value = "select distinct flagsims from workload_inventory_report_model_flagsims where workload_inventory_report_model_id = :analysisId", nativeQuery = true)
    Set<String> findAllDistinctFlagsIMSByAnalysisId(@Param("analysisId") Long analysisId);

}

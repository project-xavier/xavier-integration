package org.jboss.xavier.integrations.jpa.repository;

import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
import org.jboss.xavier.analytics.pojo.output.WorkloadInventoryReportModel;
import org.jboss.xavier.integrations.jpa.projection.InitialSavingsEstimationReportSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkloadInventoryReportRepository extends JpaRepository<WorkloadInventoryReportModel, Long>
{
    Page<WorkloadInventoryReportModel> findByAnalysisId(Long analysisId, Pageable pageable);
}

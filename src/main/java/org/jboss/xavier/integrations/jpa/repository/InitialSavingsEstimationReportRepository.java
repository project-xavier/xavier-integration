package org.jboss.xavier.integrations.jpa.repository;

import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InitialSavingsEstimationReportRepository extends JpaRepository<InitialSavingsEstimationReportModel, Long>
{
/*  TODO remove
    Iterable<InitialSavingsEstimationReportSummary> findAllReportSummaryBy();

    Page<InitialSavingsEstimationReportSummary> findAllReportSummaryBy(Pageable pageable);

    InitialSavingsEstimationReportSummary findReportSummaryById(Long id);*/

    InitialSavingsEstimationReportModel findByAnalysisOwnerAndAnalysisId(String owner, Long id);
}

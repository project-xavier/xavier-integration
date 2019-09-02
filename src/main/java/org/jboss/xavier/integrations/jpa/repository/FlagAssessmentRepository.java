package org.jboss.xavier.integrations.jpa.repository;

import org.jboss.xavier.analytics.pojo.output.workload.summary.FlagAssessmentModel;
import org.jboss.xavier.analytics.pojo.output.workload.summary.FlagModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlagAssessmentRepository extends JpaRepository<FlagAssessmentModel, Long>
{
    FlagAssessmentModel findByFlagIgnoreCase(String flag);
}

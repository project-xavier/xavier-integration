package org.jboss.xavier.integrations.jpa.repository;

import org.jboss.xavier.analytics.pojo.output.workload.summary.WorkloadsApplicationPlatformsDetectedModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkloadsApplicationPlatformsDetectedRepository extends JpaRepository<WorkloadsApplicationPlatformsDetectedModel, Long>
{
    // this name has to match the value after the '.' in the @NamedNativeQuery annotation
    List<WorkloadsApplicationPlatformsDetectedModel> calculateWorkloadApplicationPlatformsDetectedModels(@Param("analysisId") Long analysisId);

}

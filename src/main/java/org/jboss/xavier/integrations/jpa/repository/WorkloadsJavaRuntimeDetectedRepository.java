package org.jboss.xavier.integrations.jpa.repository;

import org.jboss.xavier.analytics.pojo.output.workload.summary.WorkloadsJavaRuntimeDetectedModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkloadsJavaRuntimeDetectedRepository extends JpaRepository<WorkloadsJavaRuntimeDetectedModel, Long>
{
    // this name has to match the value after the '.' in the @NamedNativeQuery annotation
    List<WorkloadsJavaRuntimeDetectedModel> calculateWorkloadsJavaRuntimeDetectedModels(@Param("analysisId") Long analysisId);

}

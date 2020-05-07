package org.jboss.xavier.integrations.jpa.repository;

import org.jboss.xavier.analytics.pojo.output.workload.summary.OSInformationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OSInformationRepository extends JpaRepository<OSInformationModel, Long>
{
    // this name has to match the value after the '.' in the @NamedNativeQuery annotation
    List<OSInformationModel> calculateOSFamiliesModels(@Param("analysisId") Long analysisId);

}

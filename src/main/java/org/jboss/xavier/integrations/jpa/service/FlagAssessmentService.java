package org.jboss.xavier.integrations.jpa.service;

import org.jboss.xavier.analytics.pojo.output.workload.summary.FlagAssessmentModel;
import org.jboss.xavier.integrations.jpa.OffsetLimitRequest;
import org.jboss.xavier.integrations.jpa.repository.FlagAssessmentRepository;
import org.jboss.xavier.integrations.route.model.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class FlagAssessmentService
{
    @Autowired
    FlagAssessmentRepository flagAssessmentRepository;

    public Page<FlagAssessmentModel> findAll(PageBean pageBean)
    {
        // Pagination
        int offset = pageBean.getOffset();
        int limit = pageBean.getLimit();
        Pageable pageable = new OffsetLimitRequest(offset, limit, null);
        return flagAssessmentRepository.findAll(pageable);
    }


}

package org.jboss.xavier.integrations.jpa.service;

import org.jboss.xavier.analytics.pojo.output.workload.summary.FlagAssessmentModel;
import org.jboss.xavier.integrations.jpa.repository.FlagAssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.Assert.assertTrue;

@Component
public class FlagAssessmentService
{
    @Autowired
    FlagAssessmentRepository flagAssessmentRepository;

    @Value("${pagination.flag-assessment.limit.max}")
    int paginationLimitMax;

    public Page<FlagAssessmentModel> findAll(int page, int size) {
        assertTrue(size <= paginationLimitMax);

        Pageable pageable = new PageRequest(page, size);
        Page<FlagAssessmentModel> result = flagAssessmentRepository.findAll(pageable);
        return result;
    }

}

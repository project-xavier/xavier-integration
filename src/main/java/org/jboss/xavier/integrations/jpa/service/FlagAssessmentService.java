package org.jboss.xavier.integrations.jpa.service;

import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.jboss.xavier.analytics.pojo.output.workload.summary.FlagAssessmentModel;
import org.jboss.xavier.integrations.jpa.repository.FlagAssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class FlagAssessmentService
{
    @Autowired
    FlagAssessmentRepository flagAssessmentRepository;

    public Page<FlagAssessmentModel> findAll(int page, int size) {
        Pageable pageable = new PageRequest(page, size);
        Page<FlagAssessmentModel> result = flagAssessmentRepository.findAll(pageable);
        return result;
    }

}

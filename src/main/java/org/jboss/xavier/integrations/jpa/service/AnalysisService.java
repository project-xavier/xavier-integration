package org.jboss.xavier.integrations.jpa.service;

import org.jboss.xavier.analytics.pojo.output.AnalysisModel;
import org.jboss.xavier.integrations.jpa.repository.AnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class AnalysisService
{
    @Autowired
    AnalysisRepository analysisRepository;

    public AnalysisModel findById(Long id)
    {
        return analysisRepository.findOne(id);
    }

    public void deleteById(Long id)
    {
        analysisRepository.delete(id);
    }

    public Page<AnalysisModel> findReports(int page, int size)
    {
        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.DESC, "id"));
        return analysisRepository.findAll(pageable);
    }

    public Page<AnalysisModel> findReports(String filterText, int page, int size)
    {
        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.DESC, "id"));
        return analysisRepository.findByReportNameIgnoreCaseContaining(filterText.trim(), pageable);
    }

}

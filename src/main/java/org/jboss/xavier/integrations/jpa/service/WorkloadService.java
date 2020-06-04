package org.jboss.xavier.integrations.jpa.service;

import org.jboss.xavier.analytics.pojo.output.workload.summary.WorkloadModel;
import org.jboss.xavier.integrations.jpa.OffsetLimitRequest;
import org.jboss.xavier.integrations.jpa.repository.WorkloadRepository;
import org.jboss.xavier.integrations.route.model.PageBean;
import org.jboss.xavier.integrations.route.model.SortBean;
import org.jboss.xavier.utils.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkloadService
{
    @Autowired
    WorkloadRepository workloadRepository;

    public List<WorkloadModel> calculateWorkloadsModels(Long analysisId)
    {
        return workloadRepository.calculateWorkloadsModels(analysisId);
    }

    public Page<WorkloadModel> findByReportAnalysisOwnerAndReportAnalysisId(String analysisOwner, Long analysisId, PageBean pageBean, List<SortBean> sortBean) {
        // Sort
        Sort sort = ConversionUtils.toSort(sortBean, WorkloadModel.SUPPORTED_SORT_FIELDS);

        // Pagination
        int offset = pageBean.getOffset();
        int limit = pageBean.getLimit();
        Pageable pageable = new OffsetLimitRequest(offset, limit, sort);

        return workloadRepository.findByReportAnalysisOwnerAndReportAnalysisId(analysisOwner, analysisId, pageable);
    }

}

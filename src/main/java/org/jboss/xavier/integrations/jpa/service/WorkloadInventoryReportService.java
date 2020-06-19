package org.jboss.xavier.integrations.jpa.service;

import org.jboss.xavier.analytics.pojo.output.WorkloadInventoryReportFiltersModel;
import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.integrations.jpa.OffsetLimitRequest;
import org.jboss.xavier.integrations.jpa.repository.WorkloadInventoryReportRepository;
import org.jboss.xavier.integrations.jpa.repository.WorkloadInventoryReportSpecs;
import org.jboss.xavier.integrations.route.model.PageBean;
import org.jboss.xavier.integrations.route.model.SortBean;
import org.jboss.xavier.integrations.route.model.WorkloadInventoryFilterBean;
import org.jboss.xavier.utils.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkloadInventoryReportService
{
    @Autowired
    WorkloadInventoryReportRepository reportRepository;

    public List<WorkloadInventoryReportModel> findByAnalysisOwnerAndAnalysisId(String analysisOwner, Long analysisId) {
        return reportRepository.findByAnalysisOwnerAndAnalysisId(analysisOwner, analysisId);
    }

    public List<WorkloadInventoryReportModel> findByAnalysisOwnerAndAnalysisId(
            String analysisOwner,
            Long analysisId,
            List<SortBean> sortBean,
            WorkloadInventoryFilterBean filterBean
    ) {
        // Sort
        Sort sort = ConversionUtils.toSort(sortBean, WorkloadInventoryReportModel.SUPPORTED_SORT_FIELDS);

        // Filtering
        Specification<WorkloadInventoryReportModel> specification = WorkloadInventoryReportSpecs.getByAnalysisOwnerAndAnalysisIdAndFilterBean(analysisOwner, analysisId, filterBean);

        return reportRepository.findAll(specification, sort);
    }

    public Page<WorkloadInventoryReportModel> findByAnalysisOwnerAndAnalysisId(
            String analysisOwner,
            Long analysisId,
            PageBean pageBean,
            List<SortBean> sortBean,
            WorkloadInventoryFilterBean filterBean
    ) {
        // Sort
        Sort sort = ConversionUtils.toSort(sortBean, WorkloadInventoryReportModel.SUPPORTED_SORT_FIELDS);

        // Pagination
        int offset = pageBean.getOffset();
        int limit = pageBean.getLimit();
        Pageable pageable = new OffsetLimitRequest(offset, limit, sort);

        // Filtering
        Specification<WorkloadInventoryReportModel> specification = WorkloadInventoryReportSpecs.getByAnalysisOwnerAndAnalysisIdAndFilterBean(analysisOwner, analysisId, filterBean);

        return reportRepository.findAll(specification, pageable);
    }

    public WorkloadInventoryReportModel findOneByOwnerAndId(String owner, Long id) {
        return reportRepository.findByAnalysisOwnerAndId(owner, id);
    }

    public WorkloadInventoryReportFiltersModel findAvailableFiltersByAnalysisId(Long analysisId) {
        WorkloadInventoryReportFiltersModel filters = new WorkloadInventoryReportFiltersModel();
        filters.setProviders(reportRepository.findAllDistinctProvidersByAnalysisId(analysisId));
        filters.setClusters(reportRepository.findAllDistinctClustersByAnalysisId(analysisId));
        filters.setDatacenters(reportRepository.findAllDistinctDatacentersByAnalysisId(analysisId));
        filters.setComplexities(reportRepository.findAllDistinctComplexitiesByAnalysisId(analysisId));
        filters.setWorkloads(reportRepository.findAllDistinctWorkloadsByAnalysisId(analysisId));
        filters.setRecommendedTargetsIMS(reportRepository.findAllDistinctRecommendedTargetsIMSByAnalysisId(analysisId));
        filters.setFlagsIMS(reportRepository.findAllDistinctFlagsIMSByAnalysisId(analysisId));
        filters.setOsNames(reportRepository.findAllDistinctOsNamesByAnalysisId(analysisId));

        return filters;
    }

    public void saveAll(List<WorkloadInventoryReportModel> workloadInventoryReportModels)
    {
        reportRepository.save(workloadInventoryReportModels);
    }
}

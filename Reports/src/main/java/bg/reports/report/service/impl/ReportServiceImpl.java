package bg.reports.report.service.impl;

import bg.reports.report.model.Report;
import bg.reports.report.repository.ReportRepository;
import bg.reports.report.service.ReportService;
import bg.reports.web.dto.*;
import bg.reports.web.mapper.DtoMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public void savePictureReport(PictureReportRequest pictureReportRequest, UUID reporterId) {
        Report report = DtoMapper.mapPictureReportRequestToReport(pictureReportRequest, reporterId);

        this.reportRepository.saveAndFlush(report);
    }

    @Override
    public void saveCommentReport(CommentReportRequest commentReportRequest, UUID reporterId) {
        Report report = DtoMapper.mapCommentReportRequestToReport(commentReportRequest, reporterId);

        this.reportRepository.saveAndFlush(report);
    }

    @Override
    public void saveUserReport(UserReportRequest userReportRequest, UUID reporterId) {
        Report report = DtoMapper.mapUserReportRequestToReport(userReportRequest, reporterId);

        this.reportRepository.saveAndFlush(report);
    }

    @Override
    public void deleteReport(UUID reportId) {
        this.reportRepository.deleteById(reportId);
    }

    @Override
    public List<PictureReportResponse> getAllPictureReports() {
        List<Report> reports = reportRepository.findByPictureIdIsNotNullAndCommentIdIsNull();
        return reports.stream()
                .map(DtoMapper::mapPictureReportToPictureReportResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentReportResponse> getAllCommentReports() {
        List<Report> reports = reportRepository.findByCommentIdIsNotNull();
        return reports.stream()
                .map(DtoMapper::mapCommentReportToCommentReportResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserReportResponse> getAllUserReports() {
        List<Report> reports = reportRepository.findByUserIdIsNotNull();
        return reports.stream()
                .map(DtoMapper::mapUserReportToUserReportResponse)
                .collect(Collectors.toList());
    }
}

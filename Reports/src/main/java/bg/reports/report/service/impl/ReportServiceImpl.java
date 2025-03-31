package bg.reports.report.service.impl;

import bg.reports.exception.ReportNotFoundException;
import bg.reports.report.model.Report;
import bg.reports.report.repository.ReportRepository;
import bg.reports.report.service.ReportService;
import bg.reports.web.dto.*;
import bg.reports.web.mapper.DtoMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public PictureReportResponse savePictureReport(PictureReportRequest pictureReportRequest, UUID reporterId) {
        Report report = DtoMapper.mapPictureReportRequestToReport(pictureReportRequest, reporterId);

        this.reportRepository.saveAndFlush(report);

        return DtoMapper.mapPictureReportToPictureReportResponse(report);
    }

    @Override
    public CommentReportResponse saveCommentReport(CommentReportRequest commentReportRequest, UUID reporterId) {
        Report report = DtoMapper.mapCommentReportRequestToReport(commentReportRequest, reporterId);

        this.reportRepository.saveAndFlush(report);

        return DtoMapper.mapCommentReportToCommentReportResponse(report);
    }

    @Override
    public UserReportResponse saveUserReport(UserReportRequest userReportRequest, UUID reporterId) {
        Report report = DtoMapper.mapUserReportRequestToReport(userReportRequest, reporterId);

        this.reportRepository.saveAndFlush(report);

        return DtoMapper.mapUserReportToUserReportResponse(report);
    }

    @Override
    public void deleteReport(UUID reportId) {
        Optional<Report> reportOptional = this.reportRepository.findById(reportId);
        if (reportOptional.isPresent()) {
            this.reportRepository.deleteById(reportId);
        } else {
            throw new ReportNotFoundException("Report with ID " + reportId + " not found");
        }
    }

    @Override
    public List<PictureReportResponse> getAllPictureReports() {
        List<Report> reports = this.reportRepository.findByPictureIdIsNotNullAndCommentIdIsNull();
        return reports.stream()
                .map(DtoMapper::mapPictureReportToPictureReportResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentReportResponse> getAllCommentReports() {
        List<Report> reports = this.reportRepository.findByCommentIdIsNotNull();
        return reports.stream()
                .map(DtoMapper::mapCommentReportToCommentReportResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserReportResponse> getAllUserReports() {
        List<Report> reports = this.reportRepository.findByUserIdIsNotNull();
        return reports.stream()
                .filter(userReport -> userReport.getCommentId() == null)
                .filter(userReport -> userReport.getPictureId() == null)
                .map(DtoMapper::mapUserReportToUserReportResponse)
                .collect(Collectors.toList());
    }
}

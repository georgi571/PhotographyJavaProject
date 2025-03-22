package bg.reports.report.service;

import bg.reports.web.dto.*;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    PictureReportResponse savePictureReport(PictureReportRequest pictureReportRequest, UUID reporterId);

    CommentReportResponse saveCommentReport(CommentReportRequest commentReportRequest, UUID reporterId);

    UserReportResponse saveUserReport(UserReportRequest userReportRequest, UUID reporterId);

    void deleteReport(UUID reportId);

    List<PictureReportResponse> getAllPictureReports();

    List<CommentReportResponse> getAllCommentReports();

    List<UserReportResponse> getAllUserReports();
}

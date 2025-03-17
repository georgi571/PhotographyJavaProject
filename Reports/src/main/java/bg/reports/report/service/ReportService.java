package bg.reports.report.service;

import bg.reports.web.dto.*;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    void savePictureReport(PictureReportRequest pictureReportRequest, UUID reporterId);

    void saveCommentReport(CommentReportRequest commentReportRequest, UUID reporterId);

    void saveUserReport(UserReportRequest userReportRequest, UUID reporterId);

    void deleteReport(UUID reportId);

    List<PictureReportResponse> getAllPictureReports();

    List<CommentReportResponse> getAllCommentReports();

    List<UserReportResponse> getAllUserReports();
}

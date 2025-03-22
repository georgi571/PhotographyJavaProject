package bg.reports.web.mapper;

import bg.reports.report.model.Report;
import bg.reports.web.dto.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class DtoMapper {

    public static Report mapCommentReportRequestToReport (CommentReportRequest commentReportRequest, UUID reporterId) {

        Report report = new Report();
        report.setChallengeId(commentReportRequest.getChallengeId());
        report.setPictureId(commentReportRequest.getPictureId());
        report.setCommentId(commentReportRequest.getCommentId());
        report.setReportReason(commentReportRequest.getReason());
        report.setReportedBy(reporterId);
        report.setCreatedAt(LocalDateTime.now());
        report.setUserId(commentReportRequest.getAuthorId());

        return report;
    }

    public static Report mapPictureReportRequestToReport (PictureReportRequest pictureReportRequest, UUID reporterId) {

        Report report = new Report();
        report.setChallengeId(pictureReportRequest.getChallengeId());
        report.setPictureId(pictureReportRequest.getPictureId());
        report.setReportReason(pictureReportRequest.getReason());
        report.setReportedBy(reporterId);
        report.setCreatedAt(LocalDateTime.now());
        report.setUserId(pictureReportRequest.getAuthorId());

        return report;
    }

    public static Report mapUserReportRequestToReport (UserReportRequest userReportRequest, UUID reporterId) {

        Report report = new Report();
        report.setUserId(userReportRequest.getUserId());
        report.setReportReason(userReportRequest.getReason());
        report.setReportedBy(reporterId);
        report.setCreatedAt(LocalDateTime.now());

        return report;
    }

    public static CommentReportResponse mapCommentReportToCommentReportResponse(Report report) {

        CommentReportResponse commentReportResponse = new CommentReportResponse();
        commentReportResponse.setId(report.getId());
        commentReportResponse.setCommentId(report.getCommentId());
        commentReportResponse.setPictureId(report.getPictureId());
        commentReportResponse.setChallengeId(report.getChallengeId());
        commentReportResponse.setReason(report.getReportReason());
        commentReportResponse.setReportedBy(report.getReportedBy());
        commentReportResponse.setAuthorId(report.getUserId());

        return commentReportResponse;
    }

    public static PictureReportResponse mapPictureReportToPictureReportResponse(Report report) {

        PictureReportResponse pictureReportResponse = new PictureReportResponse();
        pictureReportResponse.setId(report.getId());
        pictureReportResponse.setPictureId(report.getPictureId());
        pictureReportResponse.setChallengeId(report.getChallengeId());
        pictureReportResponse.setReason(report.getReportReason());
        pictureReportResponse.setReportedBy(report.getReportedBy());
        pictureReportResponse.setAuthorId(report.getUserId());

        return pictureReportResponse;
    }

    public static UserReportResponse mapUserReportToUserReportResponse(Report report) {

        UserReportResponse userReportResponse = new UserReportResponse();
        userReportResponse.setId(report.getId());
        userReportResponse.setUserId(report.getUserId());
        userReportResponse.setReason(report.getReportReason());
        userReportResponse.setReportedBy(report.getReportedBy());

        return userReportResponse;
    }
}

package bg.photographyjava.challenge.service.impl;

import bg.photographyjava.challenge.service.ChallengeService;
import org.springframework.stereotype.Service;

@Service
public class ChallengeServiceImpl implements ChallengeService {

//    private final ChallengeRepository challengeRepository;
//    private final PictureService pictureService;
//    private final UserService userService;
//    private final CommentService commentService;
//    private final ReportService reportService;
//
//    public ChallengeServiceImpl(ChallengeRepository challengeRepository, PictureService pictureService, UserService userService, CommentService commentService, ReportService reportService) {
//        this.challengeRepository = challengeRepository;
//        this.pictureService = pictureService;
//        this.userService = userService;
//        this.commentService = commentService;
//        this.reportService = reportService;
//    }
//
//
//    @Override
//    public String reportPicture(UUID challengeId, UUID pictureId, String username, String reportReason) {
//        Challenge challenge = this.challengeRepository.findById(challengeId).get();
//        UserEntity user = this.userService.getUserByUsername(username).get();
//        Picture picture = this.pictureService.getPictureById(pictureId);
//
//        Report report = new Report();
//        report.setUser(user);
//        report.setPicture(picture);
//        report.setReportReason(reportReason);
//        report.setCreatedAt(LocalDateTime.now());
//
//        this.reportService.saveReport(report);
//
//        return "Picture reported successfully";
//    }
//
//    @Override
//    public String reportComment(UUID challengeId, UUID pictureId, UUID commentId, String username, String reportReason) {
//        Challenge challenge = this.challengeRepository.findById(challengeId).get();
//        UserEntity user = this.userService.getUserByUsername(username).get();
//        Picture picture = this.pictureService.getPictureById(pictureId);
//        Comment comment = this.commentService.getCommentById(commentId);
//
//        Report report = new Report();
//        report.setUser(user);
//        report.setComment(comment);
//        report.setPicture(picture);
//        report.setReportReason(reportReason);
//        report.setCreatedAt(LocalDateTime.now());
//
//        this.reportService.saveReport(report);
//
//        return "Comment reported successfully";
//    }
}
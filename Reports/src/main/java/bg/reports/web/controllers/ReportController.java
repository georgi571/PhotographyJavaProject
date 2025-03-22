package bg.reports.web.controllers;

import bg.reports.report.service.ReportService;
import bg.reports.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/pictures")
    public ResponseEntity<List<PictureReportResponse>> getAllPictureReports() {
        return ResponseEntity.ok(this.reportService.getAllPictureReports());
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentReportResponse>> getAllCommentReports() {
        return ResponseEntity.ok(this.reportService.getAllCommentReports());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserReportResponse>> getAllUserReports() {
        return ResponseEntity.ok(this.reportService.getAllUserReports());
    }

    @PostMapping("/pictures")
    public ResponseEntity<PictureReportResponse> reportPicture(@Valid @RequestBody PictureReportRequest pictureReportRequest,
                                                             Authentication authentication) {
        UUID reporterId = (UUID) authentication.getDetails();

        PictureReportResponse pictureReportResponse = this.reportService.savePictureReport(pictureReportRequest, reporterId);

        return ResponseEntity.status(HttpStatus.CREATED).body(pictureReportResponse);
    }

    @PostMapping("/comments")
    public ResponseEntity<CommentReportResponse> reportComment(@Valid @RequestBody CommentReportRequest commentReportRequest,
                                                             Authentication authentication) {
        UUID reporterId = (UUID) authentication.getDetails();

        CommentReportResponse commentReportResponse = this.reportService.saveCommentReport(commentReportRequest, reporterId);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentReportResponse);
    }

    @PostMapping("/user")
    public ResponseEntity<UserReportResponse> reportUser(@Valid @RequestBody UserReportRequest userReportRequest,
                                                          Authentication authentication) {
        UUID reporterId = (UUID) authentication.getDetails();

        UserReportResponse userReportResponse = this.reportService.saveUserReport(userReportRequest, reporterId);

        return ResponseEntity.status(HttpStatus.CREATED).body(userReportResponse);
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID reportId) {
        this.reportService.deleteReport(reportId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

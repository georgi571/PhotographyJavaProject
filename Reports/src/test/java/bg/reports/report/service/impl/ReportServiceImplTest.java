package bg.reports.report.service.impl;

import bg.reports.exception.ReportNotFoundException;
import bg.reports.report.model.Report;
import bg.reports.report.repository.ReportRepository;
import bg.reports.web.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {
    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private UUID reporterId;
    private UUID reportId;
    private UUID reportId2;
    private Report report;
    private Report report2;

    @BeforeEach
    void setUp() {
        reporterId = UUID.randomUUID();
        reportId = UUID.randomUUID();
        reportId2 = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        String reason = "Test Reason";

        report = new Report();
        report.setId(reportId);
        report.setPictureId(pictureId);
        report.setChallengeId(challengeId);
        report.setCommentId(commentId);
        report.setReportReason(reason);
        report.setReportedBy(reporterId);
        report.setCreatedAt(createdAt);

        report2 = new Report();
        report2.setId(reportId2);
        report2.setUserId(reportId);
        report.setReportReason(reason);
        report.setReportedBy(reporterId);
        report.setCreatedAt(createdAt);
    }

    @Test
    void testSavePictureReport() {
        PictureReportRequest request = new PictureReportRequest();
        when(reportRepository.saveAndFlush(any(Report.class))).thenReturn(report);

        reportService.savePictureReport(request, reporterId);

        verify(reportRepository, times(1)).saveAndFlush(any(Report.class));
    }

    @Test
    void testSaveCommentReport() {
        CommentReportRequest request = new CommentReportRequest();
        when(reportRepository.saveAndFlush(any(Report.class))).thenReturn(report);

        reportService.saveCommentReport(request, reporterId);

        verify(reportRepository, times(1)).saveAndFlush(any(Report.class));
    }

    @Test
    void testSaveUserReport() {
        UserReportRequest request = new UserReportRequest();
        when(reportRepository.saveAndFlush(any(Report.class))).thenReturn(report);

        reportService.saveUserReport(request, reporterId);

        verify(reportRepository, times(1)).saveAndFlush(any(Report.class));
    }

    @Test
    void testDeleteReport_Success() {
        UserReportRequest request = new UserReportRequest();
        Report report = new Report();
        UUID reportId = UUID.randomUUID();

        when(reportRepository.saveAndFlush(any(Report.class))).thenReturn(report);
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        reportService.saveUserReport(request, reporterId);

        doNothing().when(reportRepository).deleteById(reportId);

        reportService.deleteReport(reportId);

        verify(reportRepository, times(1)).deleteById(reportId);
    }

    @Test
    void testDeleteReport_ReportNotFound() {
        UUID reportId = UUID.randomUUID();
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        assertThrows(ReportNotFoundException.class, () -> {
            reportService.deleteReport(reportId);
        });

        verify(reportRepository, never()).deleteById(reportId);
    }

    @Test
    void testGetAllPictureReports() {
        when(reportRepository.findByPictureIdIsNotNullAndCommentIdIsNull()).thenReturn(List.of(report));

        PictureReportResponse expectedResponse = new PictureReportResponse();
        expectedResponse.setId(report.getId());
        expectedResponse.setPictureId(report.getPictureId());
        expectedResponse.setChallengeId(report.getChallengeId());
        expectedResponse.setReason(report.getReportReason());
        expectedResponse.setReportedBy(report.getReportedBy());

        List<PictureReportResponse> responses = reportService.getAllPictureReports();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(expectedResponse.getId(), responses.getFirst().getId());
        assertEquals(expectedResponse.getPictureId(), responses.getFirst().getPictureId());
        assertEquals(expectedResponse.getChallengeId(), responses.getFirst().getChallengeId());
        assertEquals(expectedResponse.getReason(), responses.getFirst().getReason());
        assertEquals(expectedResponse.getReportedBy(), responses.getFirst().getReportedBy());
        verify(reportRepository, times(1)).findByPictureIdIsNotNullAndCommentIdIsNull();
    }

    @Test
    void testGetAllCommentReports() {
        when(reportRepository.findByCommentIdIsNotNull()).thenReturn(List.of(report));

        CommentReportResponse expectedResponse = new CommentReportResponse();
        expectedResponse.setId(report.getId());
        expectedResponse.setCommentId(report.getCommentId());
        expectedResponse.setPictureId(report.getPictureId());
        expectedResponse.setChallengeId(report.getChallengeId());
        expectedResponse.setReason(report.getReportReason());
        expectedResponse.setReportedBy(report.getReportedBy());

        List<CommentReportResponse> responses = reportService.getAllCommentReports();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(expectedResponse.getId(), responses.getFirst().getId());
        verify(reportRepository, times(1)).findByCommentIdIsNotNull();
    }

    @Test
    void testGetAllUserReports() {
        when(reportRepository.findByUserIdIsNotNull()).thenReturn(List.of(report2));

        UserReportResponse expectedResponse = new UserReportResponse();
        expectedResponse.setUserId(report2.getUserId());
        expectedResponse.setId(report2.getId());
        expectedResponse.setUserId(report2.getUserId());
        expectedResponse.setReason(report2.getReportReason());
        expectedResponse.setReportedBy(report2.getReportedBy());

        List<UserReportResponse> responses = reportService.getAllUserReports();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(expectedResponse.getId(), responses.getFirst().getId());
        verify(reportRepository, times(1)).findByUserIdIsNotNull();
    }
}
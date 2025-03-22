package bg.reports;

import bg.reports.report.repository.ReportRepository;
import bg.reports.report.service.impl.ReportServiceImpl;
import bg.reports.web.dto.CommentReportRequest;
import bg.reports.web.dto.CommentReportResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class SaveCommentReportITest {
    @Autowired
    private ReportServiceImpl reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Test
    void testSaveCommentReport_ShouldSaveReportAndReturnResponse() {
        CommentReportRequest request = new CommentReportRequest();
        UUID reporterId = UUID.randomUUID();

        CommentReportResponse response = reportService.saveCommentReport(request, reporterId);

        assertNotNull(response);
        assertEquals(request.getAuthorId(), response.getAuthorId());
        assertEquals(request.getCommentId(), response.getCommentId());
        assertEquals(request.getReason(), response.getReason());
        assertEquals(request.getChallengeId(), response.getChallengeId());
        assertEquals(request.getPictureId(), response.getPictureId());
        assertTrue(reportRepository.existsById(response.getId()));
    }
}

package bg.reports;


import bg.reports.report.repository.ReportRepository;
import bg.reports.report.service.impl.ReportServiceImpl;
import bg.reports.web.dto.PictureReportRequest;
import bg.reports.web.dto.PictureReportResponse;
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
public class SavePictureReportITest {
    @Autowired
    private ReportServiceImpl reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Test
    void testSavePictureReport_ShouldSaveReportAndReturnResponse() {
        PictureReportRequest request = new PictureReportRequest();
        UUID reporterId = UUID.randomUUID();

        PictureReportResponse response = reportService.savePictureReport(request, reporterId);

        assertNotNull(response);
        assertEquals(request.getReason(), response.getReason());
        assertEquals(request.getPictureId(), response.getPictureId());
        assertEquals(request.getAuthorId(), response.getAuthorId());
        assertEquals(request.getChallengeId(), response.getChallengeId());
        assertTrue(reportRepository.existsById(response.getId()));
    }
}

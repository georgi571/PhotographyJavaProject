package bg.reports;

import bg.reports.report.model.Report;
import bg.reports.report.repository.ReportRepository;
import bg.reports.report.service.impl.ReportServiceImpl;
import bg.reports.web.dto.PictureReportResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class GetAllPictureReportsITest {
    @Autowired
    private ReportServiceImpl reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Test
    void testGetAllPictureReports_ShouldReturnListOfPictureReports() {
        Report pictureReport = new Report();
        pictureReport.setPictureId(UUID.randomUUID());
        reportRepository.saveAndFlush(pictureReport);

        List<PictureReportResponse> response = reportService.getAllPictureReports();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertNotNull(response.getFirst().getPictureId());
    }
}

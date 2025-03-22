package bg.reports;

import bg.reports.report.repository.ReportRepository;
import bg.reports.report.service.impl.ReportServiceImpl;
import bg.reports.web.dto.UserReportRequest;
import bg.reports.web.dto.UserReportResponse;
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
public class SaveUserReportITest {
    @Autowired
    private ReportServiceImpl reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Test
    void testSaveUserReport_ShouldSaveReportAndReturnResponse() {
        UserReportRequest request = new UserReportRequest();
        UUID reporterId = UUID.randomUUID();

        UserReportResponse response = reportService.saveUserReport(request, reporterId);

        assertNotNull(response);
        assertEquals(request.getReason(), response.getReason());
        assertEquals(request.getUserId(), response.getUserId());
        assertTrue(reportRepository.existsById(response.getId()));
    }
}

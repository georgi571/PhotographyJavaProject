package bg.reports;

import bg.reports.exception.ReportNotFoundException;
import bg.reports.report.model.Report;
import bg.reports.report.repository.ReportRepository;
import bg.reports.report.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class DeleteReportITest {
    @Autowired
    private ReportServiceImpl reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Test
    void testDeleteReport_ShouldDeleteReportSuccessfully() {
        Report report = new Report();
        report = reportRepository.saveAndFlush(report);
        UUID reportId = report.getId();

        reportService.deleteReport(reportId);

        assertFalse(reportRepository.existsById(reportId));
    }

    @Test
    void testDeleteReport_ShouldThrowExceptionIfReportNotFound() {
        UUID invalidReportId = UUID.randomUUID();

        assertThrows(ReportNotFoundException.class, () -> reportService.deleteReport(invalidReportId));
    }
}

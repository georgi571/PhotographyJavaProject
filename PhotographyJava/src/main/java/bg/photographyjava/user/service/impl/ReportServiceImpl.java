package bg.photographyjava.user.service.impl;

import bg.photographyjava.user.model.Report;
import bg.photographyjava.user.repository.ReportRepository;
import bg.photographyjava.user.service.ReportService;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public void saveReport(Report report) {
        this.reportRepository.saveAndFlush(report);
    }
}

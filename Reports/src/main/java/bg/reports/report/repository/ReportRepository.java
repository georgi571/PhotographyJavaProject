package bg.reports.report.repository;

import bg.reports.report.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findByPictureIdIsNotNullAndCommentIdIsNull();

    List<Report> findByCommentIdIsNotNull();

    List<Report> findByUserIdIsNotNull();
}

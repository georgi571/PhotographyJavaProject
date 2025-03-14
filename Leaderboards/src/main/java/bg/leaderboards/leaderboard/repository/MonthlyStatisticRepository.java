package bg.leaderboards.leaderboard.repository;

import bg.leaderboards.leaderboard.model.MonthlyStatistic;
import bg.leaderboards.web.dto.LeaderboardsMonthlyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MonthlyStatisticRepository extends JpaRepository<MonthlyStatistic, UUID> {
    Optional<MonthlyStatistic> findByUsernameAndYearAndMonth(String username, int year, Month month);

    @Query("SELECT new bg.leaderboards.web.dto.LeaderboardsMonthlyResponse(m.username, m.country, m.pointsEarned) " +
            "FROM MonthlyStatistic m " +
            "WHERE m.year = :year AND m.month = :month " +
            "ORDER BY m.pointsEarned DESC")
    List<LeaderboardsMonthlyResponse> findTopUsersForMonth(@Param("year") int year, @Param("month") Month month);
}

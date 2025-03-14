package bg.leaderboards.leaderboard.repository;

import bg.leaderboards.leaderboard.model.DailyStatistic;
import bg.leaderboards.web.dto.LeaderboardsLastThirtyDaysResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DailyStatisticRepository extends JpaRepository<DailyStatistic, UUID> {
    @Modifying
    @Transactional
    @Query("DELETE FROM DailyStatistic d WHERE d.day < :cutoffDate")
    void deleteByDayBefore(@Param("cutoffDate") LocalDate cutoffDate);

    @Query(value = "SELECT new bg.leaderboards.web.dto.LeaderboardsLastThirtyDaysResponse(d.username, d.country, SUM(d.pointsEarned)) " +
            "FROM DailyStatistic d " +
            "GROUP BY d.username, d.country " +
            "ORDER BY SUM(d.pointsEarned) DESC")
    List<LeaderboardsLastThirtyDaysResponse> findPointsForLast30Days();
}

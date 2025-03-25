package bg.leaderboards.leaderboard.repository;

import bg.leaderboards.leaderboard.model.UserStatistic;
import bg.leaderboards.leaderboard.model.CountryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStatisticRepository extends JpaRepository<UserStatistic, UUID> {

    List<UserStatistic> findTop10ByCountryOrderByTotalPointsDesc(CountryEnum country);

    List<UserStatistic> findTop10ByOrderByTotalChallengesWonDesc();

    List<UserStatistic> findTop10ByOrderByTotalChallengesWonDailyDesc();

    List<UserStatistic> findTop10ByOrderByTotalChallengesWonThemedDesc();

    List<UserStatistic> findTop10ByOrderByTotalChallengesWonAdminDesc();

    Optional<UserStatistic> findByUserId(UUID userId);
}

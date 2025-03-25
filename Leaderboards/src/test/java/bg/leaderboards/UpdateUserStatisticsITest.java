package bg.leaderboards;

import bg.leaderboards.leaderboard.model.UserStatistic;
import bg.leaderboards.leaderboard.model.ChallengeType;
import bg.leaderboards.leaderboard.model.CountryEnum;
import bg.leaderboards.leaderboard.model.UserRank;
import bg.leaderboards.leaderboard.repository.UserStatisticRepository;
import bg.leaderboards.leaderboard.service.impl.UserStatisticServiceImpl;
import bg.leaderboards.web.dto.UserRegisterV1;
import bg.leaderboards.web.dto.WinnerRegisterV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class UpdateUserStatisticsITest {

    @Autowired
    private UserStatisticServiceImpl userStatisticService;

    @Autowired
    private UserStatisticRepository userStatisticRepository;

    private UUID userId;

    @BeforeEach
    public void setUp() {
        UserRegisterV1 userRegisterV1 = new UserRegisterV1();
        userId = UUID.randomUUID();
        userRegisterV1.setUserId(userId);
        userRegisterV1.setCountry(CountryEnum.BULGARIA);

        userStatisticService.saveUserInUserStatistic(userRegisterV1);
    }

    @Test
    public void updateUserStatistics_ShouldUpdateCorrectly() {
        WinnerRegisterV1 winnerRegisterV1 = new WinnerRegisterV1();
        winnerRegisterV1.setUserId(userId);
        winnerRegisterV1.setPoints(10);
        winnerRegisterV1.setType(ChallengeType.DAILY);

        userStatisticService.updateUserStatistics(winnerRegisterV1);

        Optional<UserStatistic> updatedUserStatisticOpt = userStatisticRepository.findByUserId(userId);

        assertTrue(updatedUserStatisticOpt.isPresent());

        UserStatistic updatedUserStatistic = updatedUserStatisticOpt.get();

        assertEquals(10L, updatedUserStatistic.getTotalPoints());
        assertEquals(UserRank.getRankForPoints(10), updatedUserStatistic.getUserRank());
        assertEquals(1, updatedUserStatistic.getTotalChallengesWon());
        assertEquals(1, updatedUserStatistic.getTotalChallengesWonDaily());
    }

    @Test
    public void updateUserStatistics_ShouldUpdateForDifferentChallengeTypes() {
        WinnerRegisterV1 winnerRegisterV1 = new WinnerRegisterV1();
        winnerRegisterV1.setUserId(userId);
        winnerRegisterV1.setPoints(10);
        winnerRegisterV1.setType(ChallengeType.THEMED);

        userStatisticService.updateUserStatistics(winnerRegisterV1);

        Optional<UserStatistic> updatedUserStatisticOpt = userStatisticRepository.findByUserId(userId);

        assertTrue(updatedUserStatisticOpt.isPresent());

        UserStatistic updatedUserStatistic = updatedUserStatisticOpt.get();

        assertEquals(1, updatedUserStatistic.getTotalChallengesWonThemed());
    }

    @Test
    public void updateUserStatistics_ShouldNotUpdateChallengesForNon10Points() {
        WinnerRegisterV1 winnerRegisterV1 = new WinnerRegisterV1();
        winnerRegisterV1.setUserId(userId);
        winnerRegisterV1.setPoints(5);
        winnerRegisterV1.setType(ChallengeType.DAILY);

        userStatisticService.updateUserStatistics(winnerRegisterV1);

        Optional<UserStatistic> updatedUserStatisticOpt = userStatisticRepository.findByUserId(userId);

        assertTrue(updatedUserStatisticOpt.isPresent());

        UserStatistic updatedUserStatistic = updatedUserStatisticOpt.get();

        assertEquals(0, updatedUserStatistic.getTotalChallengesWon());
    }
}

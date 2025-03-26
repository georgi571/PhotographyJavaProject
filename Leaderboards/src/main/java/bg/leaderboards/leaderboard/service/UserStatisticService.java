package bg.leaderboards.leaderboard.service;

import bg.leaderboards.leaderboard.model.UserStatistic;
import bg.leaderboards.web.dto.*;

import java.util.List;
import java.util.UUID;

public interface UserStatisticService {
    List<String> getAvailableCountries();

    List<String> getChallengeTypes();

    List<LeaderboardsUserByCountryResponse> getTop10ByCountry();

    List<LeaderboardsUserByChallengeTypeResponse> getTop10ByChallengeType();

    void updateUserStatistics(WinnerRegisterV1 winnerRegisterV1);

    void saveUserInUserStatistic(UserRegisterV1 userRegisterV1);

    UserStatistic getUserRankById(UUID userId);

    UserRankResponse getUserRankResponseById(UUID userId);

    UserChallengesResponse getUserStatisticsByUserId(UUID userId);
}

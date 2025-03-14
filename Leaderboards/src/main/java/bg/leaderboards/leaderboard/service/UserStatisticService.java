package bg.leaderboards.leaderboard.service;

import bg.leaderboards.web.dto.DailyPointsRequest;
import bg.leaderboards.web.dto.LeaderboardsUserByChallengeTypeResponse;
import bg.leaderboards.web.dto.LeaderboardsUserByCountryResponse;

import java.util.List;

public interface UserStatisticService {
    List<String> getAvailableCountries();

    List<String> getChallengeTypes();

    List<LeaderboardsUserByCountryResponse> getTop10ByCountry();

    List<LeaderboardsUserByChallengeTypeResponse> getTop10ByChallengeType();

    void updateUserStatistics(List<DailyPointsRequest> statistics);

    void saveUserInUserStatistic(String username, String country);
}

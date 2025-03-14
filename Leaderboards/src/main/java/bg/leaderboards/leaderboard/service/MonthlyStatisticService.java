package bg.leaderboards.leaderboard.service;

import bg.leaderboards.web.dto.DailyPointsRequest;
import bg.leaderboards.web.dto.LeaderboardsMonthlyResponse;

import java.time.Month;
import java.util.List;

public interface MonthlyStatisticService {
    void updateMonthlyStatistic(List<DailyPointsRequest> statistics);

    List<LeaderboardsMonthlyResponse> getTopUsersForMonth(int year, Month month);
}

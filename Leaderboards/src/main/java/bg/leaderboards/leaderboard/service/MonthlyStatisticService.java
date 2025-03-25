package bg.leaderboards.leaderboard.service;

import bg.leaderboards.web.dto.WinnerRegisterV1;
import bg.leaderboards.web.dto.LeaderboardsMonthlyResponse;

import java.time.Month;
import java.util.List;

public interface MonthlyStatisticService {
    void updateMonthlyStatistic(WinnerRegisterV1 winnerRegisterV1);

    List<LeaderboardsMonthlyResponse> getTopUsersForMonth(int year, Month month);
}

package bg.leaderboards.leaderboard.service;

import bg.leaderboards.web.dto.WinnerRegisterV1;
import bg.leaderboards.web.dto.LeaderboardsLastThirtyDaysResponse;

import java.util.List;

public interface DailyStatisticService {
    void saveDailyStatistic(WinnerRegisterV1 winnerRegisterV1);

    List<LeaderboardsLastThirtyDaysResponse> getUserByPointsForLast30Days();
}

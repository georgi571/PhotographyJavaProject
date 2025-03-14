package bg.leaderboards.leaderboard.service;

import bg.leaderboards.web.dto.DailyPointsRequest;
import bg.leaderboards.web.dto.LeaderboardsLastThirtyDaysResponse;

import java.util.List;

public interface DailyStatisticService {
    void saveDailyStatistic(List<DailyPointsRequest> statistics);

    List<LeaderboardsLastThirtyDaysResponse> getUserByPointsForLast30Days();
}

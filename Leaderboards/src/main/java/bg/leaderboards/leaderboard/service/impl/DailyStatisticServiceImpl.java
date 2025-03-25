package bg.leaderboards.leaderboard.service.impl;

import bg.leaderboards.leaderboard.model.DailyStatistic;
import bg.leaderboards.leaderboard.model.UserStatistic;
import bg.leaderboards.leaderboard.model.CountryEnum;
import bg.leaderboards.leaderboard.repository.DailyStatisticRepository;
import bg.leaderboards.leaderboard.service.DailyStatisticService;
import bg.leaderboards.leaderboard.service.UserStatisticService;
import bg.leaderboards.web.dto.WinnerRegisterV1;
import bg.leaderboards.web.dto.LeaderboardsLastThirtyDaysResponse;
import bg.leaderboards.web.mapper.DtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DailyStatisticServiceImpl implements DailyStatisticService {
    private final DailyStatisticRepository dailyStatisticRepository;
    private final UserStatisticService userStatisticService;

    public DailyStatisticServiceImpl(DailyStatisticRepository dailyStatisticRepository, UserStatisticService userStatisticService) {
        this.dailyStatisticRepository = dailyStatisticRepository;
        this.userStatisticService = userStatisticService;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void cleanOldDailyStatistics() {
        LocalDate cutoffDate = LocalDate.now().minusDays(31);
        this.dailyStatisticRepository.deleteByDayBefore(cutoffDate);
    }

    @Override
    public void saveDailyStatistic(WinnerRegisterV1 winnerRegisterV1) {
        UserStatistic userStatistic = this.userStatisticService.getUserRankById(winnerRegisterV1.getUserId());

        CountryEnum country = userStatistic.getCountry();

        DailyStatistic dailyStatistic = DtoMapper.mapWinnerRegisterV1ToDailyStatistic(winnerRegisterV1, country);

        this.dailyStatisticRepository.saveAndFlush(dailyStatistic);
    }

    @Override
    public List<LeaderboardsLastThirtyDaysResponse> getUserByPointsForLast30Days() {

        List<LeaderboardsLastThirtyDaysResponse> allUserRanks = this.dailyStatisticRepository.findPointsForLast30Days();

        int rank = 1;
        for (LeaderboardsLastThirtyDaysResponse allUserRank : allUserRanks) {
            allUserRank.setRank(rank++);
        }

        return allUserRanks;
    }
}

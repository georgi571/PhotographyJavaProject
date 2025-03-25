package bg.leaderboards.leaderboard.service.impl;

import bg.leaderboards.leaderboard.model.MonthlyStatistic;
import bg.leaderboards.leaderboard.model.UserStatistic;
import bg.leaderboards.leaderboard.model.CountryEnum;
import bg.leaderboards.leaderboard.repository.MonthlyStatisticRepository;
import bg.leaderboards.leaderboard.service.DailyStatisticService;
import bg.leaderboards.leaderboard.service.MonthlyStatisticService;
import bg.leaderboards.leaderboard.service.UserStatisticService;
import bg.leaderboards.web.dto.WinnerRegisterV1;
import bg.leaderboards.web.dto.LeaderboardsMonthlyResponse;
import bg.leaderboards.web.mapper.DtoMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class MonthlyStatisticServiceImpl implements MonthlyStatisticService {
    private final MonthlyStatisticRepository monthlyStatisticRepository;
    private final UserStatisticService userStatisticService;

    public MonthlyStatisticServiceImpl(MonthlyStatisticRepository monthlyStatisticRepository, DailyStatisticService dailyStatisticService, UserStatisticService userStatisticService) {
        this.monthlyStatisticRepository = monthlyStatisticRepository;
        this.userStatisticService = userStatisticService;
    }

    @Override
    public void updateMonthlyStatistic(WinnerRegisterV1 winnerRegisterV1) {
        LocalDate currentDate = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();

        if (currentDate.getDayOfMonth() == 1) {
            currentMonth = currentMonth.minusMonths(1);
        }

        Optional<MonthlyStatistic> optionalMonthlyStatistic = this.monthlyStatisticRepository
                .findByIdAndYearAndMonth(winnerRegisterV1.getUserId(), currentMonth.getYear(), currentMonth.getMonth());

        MonthlyStatistic monthlyStatistic;

        if (optionalMonthlyStatistic.isPresent()) {
            monthlyStatistic = optionalMonthlyStatistic.get();
        } else {
            UserStatistic userStatistic = this.userStatisticService.getUserRankById(winnerRegisterV1.getUserId());

            CountryEnum country = userStatistic.getCountry();

            monthlyStatistic = DtoMapper.mapWinnerRegisterV1ToMonthlyStatistic(winnerRegisterV1, country, currentMonth);
        }

        monthlyStatistic.setPointsEarned(monthlyStatistic.getPointsEarned() + winnerRegisterV1.getPoints());

        this.monthlyStatisticRepository.saveAndFlush(monthlyStatistic);
    }

    @Override
    public List<LeaderboardsMonthlyResponse> getTopUsersForMonth(int year, Month month) {

        List<LeaderboardsMonthlyResponse> allUsers = this.monthlyStatisticRepository.findTopUsersForMonth(year, month);

        int rank = 1;
        for (LeaderboardsMonthlyResponse user : allUsers) {
            user.setRank(rank++);
        }

        return allUsers;
    }
}

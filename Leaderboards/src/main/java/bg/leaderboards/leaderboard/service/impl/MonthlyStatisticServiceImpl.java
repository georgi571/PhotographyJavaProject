package bg.leaderboards.leaderboard.service.impl;

import bg.leaderboards.leaderboard.model.MonthlyStatistic;
import bg.leaderboards.leaderboard.property.CountryEnum;
import bg.leaderboards.leaderboard.repository.MonthlyStatisticRepository;
import bg.leaderboards.leaderboard.service.DailyStatisticService;
import bg.leaderboards.leaderboard.service.MonthlyStatisticService;
import bg.leaderboards.web.dto.DailyPointsRequest;
import bg.leaderboards.web.dto.LeaderboardsMonthlyResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class MonthlyStatisticServiceImpl implements MonthlyStatisticService {
    private final MonthlyStatisticRepository monthlyStatisticRepository;

    public MonthlyStatisticServiceImpl(MonthlyStatisticRepository monthlyStatisticRepository, DailyStatisticService dailyStatisticService) {
        this.monthlyStatisticRepository = monthlyStatisticRepository;
    }

    @Override
    public void updateMonthlyStatistic(List<DailyPointsRequest> statistics) {
        LocalDate currentDate = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();

        if (currentDate.getDayOfMonth() == 1) {
            currentMonth = currentMonth.minusMonths(1);
        }

        for (DailyPointsRequest statistic : statistics) {
            Optional<MonthlyStatistic> optionalMonthlyStatistic = monthlyStatisticRepository
                    .findByUsernameAndYearAndMonth(statistic.getUsername(), currentMonth.getYear(), currentMonth.getMonth());

            MonthlyStatistic monthlyStatistic;

            if (optionalMonthlyStatistic.isPresent()) {
                monthlyStatistic = optionalMonthlyStatistic.get();
            } else {
                monthlyStatistic = new MonthlyStatistic();
                monthlyStatistic.setUsername(statistic.getUsername());
                monthlyStatistic.setCountry(CountryEnum.valueOf(statistic.getCountry()));
                monthlyStatistic.setYear(currentMonth.getYear());
                monthlyStatistic.setMonth(currentMonth.getMonth());
                monthlyStatistic.setPointsEarned(0);
            }

            monthlyStatistic.setPointsEarned(monthlyStatistic.getPointsEarned() + statistic.getPoints());

            this.monthlyStatisticRepository.saveAndFlush(monthlyStatistic);
        }
    }

    @Override
    public List<LeaderboardsMonthlyResponse> getTopUsersForMonth(int year, Month month) {

        List<LeaderboardsMonthlyResponse> allUsers =
                this.monthlyStatisticRepository.findTopUsersForMonth(year, month);

        int rank = 1;
        for (LeaderboardsMonthlyResponse user : allUsers) {
            user.setRank(rank++);
        }

        return allUsers;
    }
}

package bg.leaderboards.web.mapper;

import bg.leaderboards.leaderboard.model.DailyStatistic;
import bg.leaderboards.leaderboard.model.MonthlyStatistic;
import bg.leaderboards.leaderboard.model.UserStatistic;
import bg.leaderboards.leaderboard.model.CountryEnum;
import bg.leaderboards.leaderboard.model.UserRank;
import bg.leaderboards.web.dto.*;

import java.time.LocalDate;
import java.time.YearMonth;

public class DtoMapper {

    public static LeaderboardsUserByCountryResponse mapUserStatisticToLeaderboardsUserByCountryResponse(UserStatistic userStatistic, int ranking) {

        LeaderboardsUserByCountryResponse rank = new LeaderboardsUserByCountryResponse();
        rank.setCountry(userStatistic.getCountry().getCountryName());
        rank.setUserId(userStatistic.getUserId());
        rank.setPoints(userStatistic.getTotalPoints());
        rank.setRank(ranking);

        return rank;
    }

    public static LeaderboardsUserByChallengeTypeResponse mapUserStatisticToLeaderboardsUserByChallengeTypeAllResponse(UserStatistic userStatistic, String type, int ranking) {

        LeaderboardsUserByChallengeTypeResponse rank = new LeaderboardsUserByChallengeTypeResponse();
        rank.setCountry(userStatistic.getCountry().getCountryName());
        rank.setUserId(userStatistic.getUserId());
        rank.setNumberOfWinChallenges(userStatistic.getTotalChallengesWon());
        rank.setRank(ranking);
        rank.setChallengeType(type);

        return rank;
    }

    public static LeaderboardsUserByChallengeTypeResponse mapUserStatisticToLeaderboardsUserByChallengeTypeDailyResponse(UserStatistic userStatistic, String type, int ranking) {

        LeaderboardsUserByChallengeTypeResponse rank = new LeaderboardsUserByChallengeTypeResponse();
        rank.setCountry(userStatistic.getCountry().getCountryName());
        rank.setUserId(userStatistic.getUserId());
        rank.setNumberOfWinChallenges(userStatistic.getTotalChallengesWonDaily());
        rank.setRank(ranking);
        rank.setChallengeType(type);

        return rank;
    }

    public static LeaderboardsUserByChallengeTypeResponse mapUserStatisticToLeaderboardsUserByChallengeTypeThemedResponse(UserStatistic userStatistic, String type, int ranking) {

        LeaderboardsUserByChallengeTypeResponse rank = new LeaderboardsUserByChallengeTypeResponse();
        rank.setCountry(userStatistic.getCountry().getCountryName());
        rank.setUserId(userStatistic.getUserId());
        rank.setNumberOfWinChallenges(userStatistic.getTotalChallengesWonThemed());
        rank.setRank(ranking);
        rank.setChallengeType(type);

        return rank;
    }

    public static LeaderboardsUserByChallengeTypeResponse mapUserStatisticToLeaderboardsUserByChallengeTypeAdminResponse(UserStatistic userStatistic, String type, int ranking) {

        LeaderboardsUserByChallengeTypeResponse rank = new LeaderboardsUserByChallengeTypeResponse();
        rank.setCountry(userStatistic.getCountry().getCountryName());
        rank.setUserId(userStatistic.getUserId());
        rank.setNumberOfWinChallenges(userStatistic.getTotalChallengesWonAdmin());
        rank.setRank(ranking);
        rank.setChallengeType(type);

        return rank;
    }

    public static DailyStatistic mapWinnerRegisterV1ToDailyStatistic(WinnerRegisterV1 winnerRegisterV1, CountryEnum country) {

        DailyStatistic dailyStatistic = new DailyStatistic();
        dailyStatistic.setUserId(winnerRegisterV1.getUserId());
        dailyStatistic.setPointsEarned(winnerRegisterV1.getPoints());
        dailyStatistic.setDay(LocalDate.now().minusDays(1));
        dailyStatistic.setCountry(country);

        return dailyStatistic;
    }

    public static UserStatistic mapUserRegisterV1ToUserStatistic(UserRegisterV1 userRegisterV1) {

        UserStatistic userStatistic = new UserStatistic();
        userStatistic.setUserId(userRegisterV1.getUserId());
        userStatistic.setCountry(userRegisterV1.getCountry());
        userStatistic.setTotalPoints(0);
        userStatistic.setTotalChallengesWon(0);
        userStatistic.setTotalChallengesWonDaily(0);
        userStatistic.setTotalChallengesWonThemed(0);
        userStatistic.setTotalChallengesWonAdmin(0);
        userStatistic.setUserRank(UserRank.getRankForPoints(0));

        return userStatistic;
    }

    public static MonthlyStatistic mapWinnerRegisterV1ToMonthlyStatistic(WinnerRegisterV1 statistic, CountryEnum country, YearMonth currentMonth) {

        MonthlyStatistic monthlyStatistic = new MonthlyStatistic();
        monthlyStatistic.setUserId(statistic.getUserId());
        monthlyStatistic.setCountry(country);
        monthlyStatistic.setYear(currentMonth.getYear());
        monthlyStatistic.setMonth(currentMonth.getMonth());
        monthlyStatistic.setPointsEarned(0);

        return monthlyStatistic;
    }

    public static UserRankResponse mapUserStatisticToUserRankResponse(UserStatistic userStatistic) {

        UserRankResponse userRankResponse = new UserRankResponse();
        userRankResponse.setUserId(userStatistic.getUserId());
        userRankResponse.setTotalPoints(userStatistic.getTotalPoints());
        userRankResponse.setUserRank(userStatistic.getUserRank());

        return userRankResponse;
    }
}

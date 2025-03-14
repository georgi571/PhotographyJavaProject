package bg.leaderboards.web.mapper;

import bg.leaderboards.leaderboard.model.DailyStatistic;
import bg.leaderboards.leaderboard.model.UserStatistic;
import bg.leaderboards.leaderboard.property.CountryEnum;
import bg.leaderboards.web.dto.DailyPointsRequest;
import bg.leaderboards.web.dto.LeaderboardsUserByChallengeTypeResponse;
import bg.leaderboards.web.dto.LeaderboardsUserByCountryResponse;

import java.time.LocalDate;

public class DtoMapper {

    public static LeaderboardsUserByCountryResponse mapUserStatisticToLeaderboardsUserByCountryResponse(UserStatistic userStatistic, int ranking) {

        LeaderboardsUserByCountryResponse rank = new LeaderboardsUserByCountryResponse();
        rank.setCountry(userStatistic.getCountry().getCountryName());
        rank.setUsername(userStatistic.getUsername());
        rank.setPoints(userStatistic.getTotalPoints());
        rank.setRank(ranking);

        return rank;
    }

    public static LeaderboardsUserByChallengeTypeResponse mapUserStatisticToLeaderboardsUserByChallengeTypeAllResponse(UserStatistic userStatistic, String type, int ranking) {

        LeaderboardsUserByChallengeTypeResponse rank = new LeaderboardsUserByChallengeTypeResponse();
        rank.setCountry(userStatistic.getCountry().getCountryName());
        rank.setUsername(userStatistic.getUsername());
        rank.setNumberOfWinChallenges(userStatistic.getTotalChallengesWon());
        rank.setRank(ranking);
        rank.setChallengeType(type);

        return rank;
    }

    public static LeaderboardsUserByChallengeTypeResponse mapUserStatisticToLeaderboardsUserByChallengeTypeDailyResponse(UserStatistic userStatistic, String type, int ranking) {

        LeaderboardsUserByChallengeTypeResponse rank = new LeaderboardsUserByChallengeTypeResponse();
        rank.setCountry(userStatistic.getCountry().getCountryName());
        rank.setUsername(userStatistic.getUsername());
        rank.setNumberOfWinChallenges(userStatistic.getTotalChallengesWonDaily());
        rank.setRank(ranking);
        rank.setChallengeType(type);

        return rank;
    }

    public static LeaderboardsUserByChallengeTypeResponse mapUserStatisticToLeaderboardsUserByChallengeTypeThemedResponse(UserStatistic userStatistic, String type, int ranking) {

        LeaderboardsUserByChallengeTypeResponse rank = new LeaderboardsUserByChallengeTypeResponse();
        rank.setCountry(userStatistic.getCountry().getCountryName());
        rank.setUsername(userStatistic.getUsername());
        rank.setNumberOfWinChallenges(userStatistic.getTotalChallengesWonThemed());
        rank.setRank(ranking);
        rank.setChallengeType(type);

        return rank;
    }

    public static LeaderboardsUserByChallengeTypeResponse mapUserStatisticToLeaderboardsUserByChallengeTypeAdminResponse(UserStatistic userStatistic, String type, int ranking) {

        LeaderboardsUserByChallengeTypeResponse rank = new LeaderboardsUserByChallengeTypeResponse();
        rank.setCountry(userStatistic.getCountry().getCountryName());
        rank.setUsername(userStatistic.getUsername());
        rank.setNumberOfWinChallenges(userStatistic.getTotalChallengesWonAdmin());
        rank.setRank(ranking);
        rank.setChallengeType(type);

        return rank;
    }

    public static DailyStatistic mapDailyPointsRequestToDailyStatistic(DailyPointsRequest dailyPointsRequest) {

        DailyStatistic dailyStatistic = new DailyStatistic();
        dailyStatistic.setUsername(dailyPointsRequest.getUsername());
        dailyStatistic.setCountry(CountryEnum.valueOf(dailyPointsRequest.getCountry()));
        dailyStatistic.setPointsEarned(dailyPointsRequest.getPoints());
        dailyStatistic.setDay(LocalDate.now().minusDays(1));

        return dailyStatistic;
    }
}

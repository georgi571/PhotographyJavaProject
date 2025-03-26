package bg.leaderboards.leaderboard.service.impl;

import bg.leaderboards.exception.ResourceNotFoundException;
import bg.leaderboards.leaderboard.model.UserStatistic;
import bg.leaderboards.leaderboard.model.ChallengeType;
import bg.leaderboards.leaderboard.model.CountryEnum;
import bg.leaderboards.leaderboard.model.UserRank;
import bg.leaderboards.leaderboard.repository.UserStatisticRepository;
import bg.leaderboards.leaderboard.service.UserStatisticService;
import bg.leaderboards.web.dto.*;
import bg.leaderboards.web.mapper.DtoMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserStatisticServiceImpl implements UserStatisticService {
    private final UserStatisticRepository userStatisticRepository;

    public UserStatisticServiceImpl(UserStatisticRepository userStatisticRepository) {
        this.userStatisticRepository = userStatisticRepository;
    }

    @Override
    public List<String> getAvailableCountries() {
        return CountryEnum.getCountryNames();
    }

    @Override
    public List<String> getChallengeTypes() {
        return Stream.of(ChallengeType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaderboardsUserByCountryResponse> getTop10ByCountry() {

        List<LeaderboardsUserByCountryResponse> top10ByCountry = new ArrayList<>();

        for (CountryEnum country : CountryEnum.getCountries()) {
            List<UserStatistic> userFromCountry = this.userStatisticRepository.findTop10ByCountryOrderByTotalPointsDesc(country);

            int rank = 1;
            for (UserStatistic user : userFromCountry) {
                top10ByCountry.add(DtoMapper.mapUserStatisticToLeaderboardsUserByCountryResponse(user, rank++));
            }
        }

        return top10ByCountry;
    }

    @Override
    public List<LeaderboardsUserByChallengeTypeResponse> getTop10ByChallengeType() {
        List<LeaderboardsUserByChallengeTypeResponse> top10ByChallengeType = new ArrayList<>();

        for (ChallengeType challengeType : ChallengeType.values()) {
            List<UserStatistic> usersByChallenge = switch (challengeType) {
                case DAILY -> this.userStatisticRepository.findTop10ByOrderByTotalChallengesWonDailyDesc();
                case THEMED -> this.userStatisticRepository.findTop10ByOrderByTotalChallengesWonThemedDesc();
                case ADMIN -> this.userStatisticRepository.findTop10ByOrderByTotalChallengesWonAdminDesc();
            };

            int rank = 1;
            for (UserStatistic user : usersByChallenge) {
                LeaderboardsUserByChallengeTypeResponse rankResponse = mapUserByChallengeType(user, challengeType, rank++);
                top10ByChallengeType.add(rankResponse);
            }
        }

        List<UserStatistic> usersByAllChallenges = this.userStatisticRepository.findTop10ByOrderByTotalChallengesWonDesc();

        int rank = 1;
        for (UserStatistic user : usersByAllChallenges) {
            top10ByChallengeType.add(
                    DtoMapper.mapUserStatisticToLeaderboardsUserByChallengeTypeAllResponse(user, "ALL", rank++)
            );
        }

        return top10ByChallengeType;
    }

    @Override
    public void updateUserStatistics(WinnerRegisterV1 winnerRegisterV1) {
        UserStatistic userStatistic = this.userStatisticRepository.findByUserId(winnerRegisterV1.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("UserStatistic not found for userId: " + winnerRegisterV1.getUserId()));

        long newTotalPoints = userStatistic.getTotalPoints() + winnerRegisterV1.getPoints();
        userStatistic.setTotalPoints(newTotalPoints);
        userStatistic.setUserRank(UserRank.getRankForPoints((int) newTotalPoints));

        if (winnerRegisterV1.getPoints() == 10) {
            userStatistic.setTotalChallengesWon(userStatistic.getTotalChallengesWon() + 1);

            switch (winnerRegisterV1.getType()) {
                case DAILY -> userStatistic.setTotalChallengesWonDaily(userStatistic.getTotalChallengesWonDaily() + 1);
                case THEMED -> userStatistic.setTotalChallengesWonThemed(userStatistic.getTotalChallengesWonThemed() + 1);
                case ADMIN -> userStatistic.setTotalChallengesWonAdmin(userStatistic.getTotalChallengesWonAdmin() + 1);
            }
        }

        this.userStatisticRepository.saveAndFlush(userStatistic);
    }

    @Override
    public void saveUserInUserStatistic(UserRegisterV1 userRegisterV1) {
        UserStatistic userStatistic = DtoMapper.mapUserRegisterV1ToUserStatistic(userRegisterV1);

        this.userStatisticRepository.saveAndFlush(userStatistic);
    }

    @Override
    public UserStatistic getUserRankById(UUID userId) {
        return this.userStatisticRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserStatistic not found for userId: " + userId));
    }

    @Override
    public UserRankResponse getUserRankResponseById(UUID userId) {

        return DtoMapper.mapUserStatisticToUserRankResponse(this.getUserRankById(userId));
    }

    @Override
    public UserChallengesResponse getUserStatisticsByUserId(UUID userId) {
        UserStatistic userStatistic = this.userStatisticRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserStatistic not found for userId: " + userId));

        return DtoMapper.mapUserStatisticToUserChallengesResponse(userStatistic);
    }

    public LeaderboardsUserByChallengeTypeResponse mapUserByChallengeType(UserStatistic userStatistic, ChallengeType challengeType, int ranking) {
        return switch (challengeType) {
            case DAILY ->
                    DtoMapper.mapUserStatisticToLeaderboardsUserByChallengeTypeDailyResponse(userStatistic, challengeType.name(), ranking);
            case THEMED ->
                    DtoMapper.mapUserStatisticToLeaderboardsUserByChallengeTypeThemedResponse(userStatistic, challengeType.name(), ranking);
            case ADMIN ->
                    DtoMapper.mapUserStatisticToLeaderboardsUserByChallengeTypeAdminResponse(userStatistic, challengeType.name(), ranking);
        };
    }

}

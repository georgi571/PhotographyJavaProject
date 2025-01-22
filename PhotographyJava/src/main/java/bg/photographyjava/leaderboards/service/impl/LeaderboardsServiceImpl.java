package bg.photographyjava.leaderboards.service.impl;

import bg.photographyjava.challenge.model.Challenge;
import bg.photographyjava.challenge.property.enums.ChallengeType;
import bg.photographyjava.challenge.service.ChallengeService;
import bg.photographyjava.leaderboards.service.LeaderboardsService;
import bg.photographyjava.user.model.Country;
import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.user.property.enums.CountryEnum;
import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.dto.LeaderboardsUserByChallengeType;
import bg.photographyjava.web.dto.LeaderboardsUserByCountryDTO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LeaderboardsServiceImpl implements LeaderboardsService {

    private final UserService userService;
    private final ChallengeService challengeService;

    public LeaderboardsServiceImpl(UserService userService, ChallengeService challengeService) {
        this.userService = userService;
        this.challengeService = challengeService;
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
    public List<LeaderboardsUserByCountryDTO> getTopUsersFromAllCountries() {
        List<UserEntity> allUsers = this.userService.getAllUsersForCountries();

        Map<Country, List<UserEntity>> usersByCountry = allUsers.stream()
                .collect(Collectors.groupingBy(UserEntity::getCountry));

        List<LeaderboardsUserByCountryDTO> topUsers = new ArrayList<>();

        for (Map.Entry<Country, List<UserEntity>> entry : usersByCountry.entrySet()) {
            Country country = entry.getKey();
            List<UserEntity> usersInCountry = entry.getValue();

            List<UserEntity> top10Users = usersInCountry.stream()
                    .sorted((u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()))
                    .limit(10)
                    .toList();

            for (int i = 0; i < top10Users.size(); i++) {
                UserEntity user = top10Users.get(i);
                LeaderboardsUserByCountryDTO userDTO = new LeaderboardsUserByCountryDTO(
                        user.getId(),
                        user.getUsername(),
                        country.getName().getCountryName(),
                        user.getPoints(),
                        i + 1
                );
                topUsers.add(userDTO);
            }
        }

        return topUsers;
    }

    @Override
    public Map<ChallengeType, List<LeaderboardsUserByChallengeType>> getTopUsersForEachChallengeType() {
        Map<ChallengeType, List<LeaderboardsUserByChallengeType>> topUsersByChallengeType = new HashMap<>();
        for (ChallengeType challengeType : ChallengeType.values()) {
            List<Challenge> challenges = this.challengeService.findByType(challengeType);
            Map<UserEntity, Long> userWinsCountMap = new HashMap<>();

            for (Challenge challenge : challenges) {
                UserEntity user = challenge.getWinners().getFirst().getUser();
                userWinsCountMap.put(user, userWinsCountMap.getOrDefault(user, 0L) + 1);
            }

            List<LeaderboardsUserByChallengeType> topUsersForType = new ArrayList<>();
            List<LeaderboardsUserByChallengeType> finalTopUsersForType = topUsersForType;
            userWinsCountMap.forEach((user, winCount) -> {
                LeaderboardsUserByChallengeType userDTO = new LeaderboardsUserByChallengeType();
                    userDTO.setId(user.getId());
                    userDTO.setUsername(user.getUsername());
                    userDTO.setNumberOfWinChallenges(winCount);
                    userDTO.setChallengeType(challengeType.name());
                    finalTopUsersForType.add(userDTO);
            });

            topUsersForType.sort(Comparator.comparingLong(LeaderboardsUserByChallengeType::getNumberOfWinChallenges).reversed());

            topUsersForType = topUsersForType.stream().limit(10).collect(Collectors.toList());

            topUsersByChallengeType.put(challengeType, topUsersForType);
        }

        return topUsersByChallengeType;
    }

//    public List<User> getPhotographersOfMonth() {
//    }
//
//    public List<User> getActiveUsers() {
//    }
//
//    public List<User> getRisingStars() {
//    }
}

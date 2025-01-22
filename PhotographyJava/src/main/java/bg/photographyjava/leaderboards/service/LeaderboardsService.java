package bg.photographyjava.leaderboards.service;

import bg.photographyjava.challenge.property.enums.ChallengeType;
import bg.photographyjava.web.dto.LeaderboardsUserByChallengeType;
import bg.photographyjava.web.dto.LeaderboardsUserByCountryDTO;

import java.util.List;
import java.util.Map;

public interface LeaderboardsService {

    List<String> getAvailableCountries();

    List<String> getChallengeTypes();

    List<LeaderboardsUserByCountryDTO> getTopUsersFromAllCountries();

    Map<ChallengeType, List<LeaderboardsUserByChallengeType>> getTopUsersForEachChallengeType();
}

package bg.photographyjava.web.controllers;

import bg.photographyjava.challenge.property.enums.ChallengeType;
import bg.photographyjava.leaderboards.service.LeaderboardsService;
import bg.photographyjava.web.dto.LeaderboardsUserByChallengeType;
import bg.photographyjava.web.dto.LeaderboardsUserByCountryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaderboards")
public class LeaderBoardsController {

    private final LeaderboardsService leaderboardsService;

    public LeaderBoardsController(LeaderboardsService leaderboardsService) {
        this.leaderboardsService = leaderboardsService;
    }

    @GetMapping("/countries-choice")
    public ResponseEntity<?> getCountries() {
        List<String> countries = this.leaderboardsService.getAvailableCountries();
        return ResponseEntity.ok(countries);
    }

    @GetMapping("/challenge-types")
    public ResponseEntity<List<String>> getChallengeTypes() {
        List<String> challengeTypes = this.leaderboardsService.getChallengeTypes();
        return ResponseEntity.ok(challengeTypes);
    }

    @GetMapping("/country")
    public ResponseEntity<?> getTopUsersFromAllCountries() {
        List<LeaderboardsUserByCountryDTO> topUsers = this.leaderboardsService.getTopUsersFromAllCountries();
        return ResponseEntity.ok(topUsers);
    }

    @GetMapping("/challenges")
    public ResponseEntity<?> getTopUsersForEachChallengeType() {
        Map<ChallengeType, List<LeaderboardsUserByChallengeType>> topUsersForEachChallengeType = this.leaderboardsService.getTopUsersForEachChallengeType();
        return ResponseEntity.ok(topUsersForEachChallengeType);
    }

//    @GetMapping("/month")
//    public ResponseEntity<?> getPhotographersOfMonth() {
//        List<User> users = this.leaderboardsService.getPhotographersOfMonth();
//        return ResponseEntity.ok(users);
//    }
//
//    @GetMapping("/active")
//    public ResponseEntity<?> getActiveUsers() {
//        List<User> users = this.leaderboardsService.getActiveUsers();
//        return ResponseEntity.ok(users);
//    }
//
//    @GetMapping("/rising")
//    public ResponseEntity<?> getRisingStars() {
//        List<User> users = this.leaderboardsService.getRisingStars();
//        return ResponseEntity.ok(users);
//    }
}

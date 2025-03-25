package bg.leaderboards.web.controllers;

import bg.leaderboards.leaderboard.service.DailyStatisticService;
import bg.leaderboards.leaderboard.service.MonthlyStatisticService;
import bg.leaderboards.leaderboard.service.UserStatisticService;
import bg.leaderboards.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Month;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leaderboards")
public class LeaderboardsController {

    private final UserStatisticService userStatisticService;
    private final DailyStatisticService dailyStatisticService;
    private final MonthlyStatisticService monthlyStatisticService;

    public LeaderboardsController(UserStatisticService userStatisticService, DailyStatisticService dailyStatisticService, MonthlyStatisticService monthlyStatisticService) {
        this.userStatisticService = userStatisticService;
        this.dailyStatisticService = dailyStatisticService;
        this.monthlyStatisticService = monthlyStatisticService;
    }

    @GetMapping()
    public ResponseEntity<Void> getLeaderboardsPage() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/countries-choice")
    public ResponseEntity<List<String>> getCountries() {
        List<String> countries = this.userStatisticService.getAvailableCountries();
        return ResponseEntity.ok(countries);
    }

    @GetMapping("/challenge-types")
    public ResponseEntity<List<String>> getChallengeTypes() {
        List<String> challengeTypes = this.userStatisticService.getChallengeTypes();
        return ResponseEntity.ok(challengeTypes);
    }

    @GetMapping("/country")
    public ResponseEntity<List<LeaderboardsUserByCountryResponse>> getTopUsersFromAllCountries() {
        List<LeaderboardsUserByCountryResponse> topUsers = this.userStatisticService.getTop10ByCountry();
        return ResponseEntity.ok(topUsers);
    }

    @GetMapping("/challenges")
    public ResponseEntity<List<LeaderboardsUserByChallengeTypeResponse>> getTopUsersForEachChallengeType() {
        List<LeaderboardsUserByChallengeTypeResponse> topUsersForEachChallengeType = this.userStatisticService.getTop10ByChallengeType();
        return ResponseEntity.ok(topUsersForEachChallengeType);
    }

    @GetMapping("/month")
    public ResponseEntity<List<LeaderboardsMonthlyResponse>> getPhotographersOfMonth(@RequestParam int year,
                                                                                     @RequestParam Month month) {
        List<LeaderboardsMonthlyResponse> users = this.monthlyStatisticService.getTopUsersForMonth(year, month);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/rising")
    public ResponseEntity<List<LeaderboardsLastThirtyDaysResponse>> getRisingStars() {
        List<LeaderboardsLastThirtyDaysResponse> users = this.dailyStatisticService.getUserByPointsForLast30Days();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/rank")
    public ResponseEntity<UserRankResponse> getUserRank(@RequestParam UUID userId) {
        UserRankResponse userRankResponse = this.userStatisticService.getUserRankResponseById(userId);
        return ResponseEntity.ok(userRankResponse);
    }
}

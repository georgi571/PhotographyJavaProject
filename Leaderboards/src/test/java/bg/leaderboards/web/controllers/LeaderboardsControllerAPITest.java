package bg.leaderboards.web.controllers;

import bg.leaderboards.leaderboard.model.CountryEnum;
import bg.leaderboards.leaderboard.model.UserRank;
import bg.leaderboards.leaderboard.service.DailyStatisticService;
import bg.leaderboards.leaderboard.service.MonthlyStatisticService;
import bg.leaderboards.leaderboard.service.UserStatisticService;
import bg.leaderboards.web.dto.*;
import bg.leaderboards.web.filter.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaderboardsController.class)
class LeaderboardsControllerAPITest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserStatisticService userStatisticService;

    @MockitoBean
    private DailyStatisticService dailyStatisticService;

    @MockitoBean
    private MonthlyStatisticService monthlyStatisticService;

    @MockitoBean
    private JWTService jwtService;

    @BeforeEach
    void setUp() {

        when(jwtService.validateToken(anyString())).thenReturn(true);
        when(jwtService.extractUsername(anyString())).thenReturn("testUser");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testUser", null, AuthorityUtils.createAuthorityList(
                "ROLE_USER", "PERMISSION_deletePicture", "ROLE_ADMIN", "PERMISSION_deleteMessage",
                "PERMISSION_banUsers"
        ));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetCountries_ReturnsCountryList() throws Exception {
        List<String> countries = Arrays.asList("BULGARIA", "MALAYSIA");
        when(userStatisticService.getAvailableCountries()).thenReturn(countries);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/leaderboards/countries-choice")
                        .header("Authorization", "Bearer mock-valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("BULGARIA"))
                .andExpect(jsonPath("$[1]").value("MALAYSIA"));
    }

    @Test
    void testGetChallengeTypes_ReturnsChallengeList() throws Exception {
        List<String> challenges = Arrays.asList("DAILY", "THEMED");
        when(userStatisticService.getChallengeTypes()).thenReturn(challenges);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/leaderboards/challenge-types")
                        .header("Authorization", "Bearer mock-valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("DAILY"))
                .andExpect(jsonPath("$[1]").value("THEMED"));
    }

    @Test
    void testGetTopUsersFromAllCountries_ReturnsLeaderboard() throws Exception {
        LeaderboardsUserByCountryResponse response = new LeaderboardsUserByCountryResponse();
        response.setUserId(UUID.randomUUID());
        response.setCountry("BULGARIA");
        response.setPoints(100);
        response.setRank(1);

        when(userStatisticService.getTop10ByCountry()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/leaderboards/country")
                        .header("Authorization", "Bearer mock-valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].country").value("BULGARIA"))
                .andExpect(jsonPath("$[0].points").value(100))
                .andExpect(jsonPath("$[0].rank").value(1));
    }

    @Test
    void testGetTopUsersForEachChallengeType_ReturnsUsers() throws Exception {
        LeaderboardsUserByChallengeTypeResponse response = new LeaderboardsUserByChallengeTypeResponse();
        response.setUserId(UUID.randomUUID());
        response.setCountry("BULGARIA");
        response.setNumberOfWinChallenges(2);
        response.setChallengeType("DAILY");
        response.setRank(1);

        when(userStatisticService.getTop10ByChallengeType()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/leaderboards/challenges")
                        .header("Authorization", "Bearer mock-valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].challengeType").value("DAILY"))
                .andExpect(jsonPath("$[0].numberOfWinChallenges").value(2));
    }

    @Test
    void testGetPhotographersOfMonth_ReturnsUsers() throws Exception {
        LeaderboardsMonthlyResponse response = new LeaderboardsMonthlyResponse();
        response.setUserId(UUID.randomUUID());
        response.setCountry(CountryEnum.BULGARIA);
        response.setPoints(500);
        response.setRank(1);

        when(monthlyStatisticService.getTopUsersForMonth(2024, Month.JANUARY)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/leaderboards/month?year=2024&month=JANUARY")
                        .header("Authorization", "Bearer mock-valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].country").value("BULGARIA"))
                .andExpect(jsonPath("$[0].points").value(500))
                .andExpect(jsonPath("$[0].rank").value(1));
    }

    @Test
    void testGetRisingStars_ReturnsUsers() throws Exception {
        LeaderboardsLastThirtyDaysResponse response = new LeaderboardsLastThirtyDaysResponse();
        response.setUserId(UUID.randomUUID());
        response.setCountry(CountryEnum.BULGARIA);
        response.setPoints(500);
        response.setRank(1);

        when(dailyStatisticService.getUserByPointsForLast30Days()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/leaderboards/rising")
                        .header("Authorization", "Bearer mock-valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].country").value("BULGARIA"))
                .andExpect(jsonPath("$[0].points").value(500))
                .andExpect(jsonPath("$[0].rank").value(1));
    }

    @Test
    void testGetUserRank_ReturnsRank() throws Exception {
        UUID userId = UUID.randomUUID();
        UserRankResponse response = new UserRankResponse();
        response.setUserId(userId);
        response.setTotalPoints(500);
        response.setUserRank(UserRank.INTERMEDIATE);

        when(userStatisticService.getUserRankResponseById(userId)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/leaderboards/rank")
                        .header("Authorization", "Bearer mock-valid-token")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.totalPoints").value(500))
                .andExpect(jsonPath("$.userRank").value("INTERMEDIATE"));
    }
}
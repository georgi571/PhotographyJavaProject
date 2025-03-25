package bg.leaderboards.leaderboard.service.impl;

import bg.leaderboards.leaderboard.model.UserStatistic;
import bg.leaderboards.leaderboard.model.ChallengeType;
import bg.leaderboards.leaderboard.model.CountryEnum;
import bg.leaderboards.leaderboard.model.UserRank;
import bg.leaderboards.leaderboard.repository.UserStatisticRepository;
import bg.leaderboards.web.dto.*;
import bg.leaderboards.web.mapper.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserStatisticServiceImplUTest {

    @Mock
    private UserStatisticRepository userStatisticRepository;

    @InjectMocks
    private UserStatisticServiceImpl userStatisticService;

    private UserStatistic userStatistic;

    @BeforeEach
    public void setUp() {
        userStatistic = new UserStatistic();
        userStatistic.setUserId(UUID.randomUUID());
        userStatistic.setTotalPoints(100);
        userStatistic.setUserRank(UserRank.BEGINNER);
        userStatistic.setCountry(CountryEnum.BULGARIA);
    }

    @Test
    public void testGetAvailableCountries() {
        List<String> countries = Arrays.asList(CountryEnum.BULGARIA.name(), CountryEnum.MALAYSIA.name());

        try (MockedStatic<CountryEnum> mockedStatic = mockStatic(CountryEnum.class)) {
            mockedStatic.when(CountryEnum::getCountryNames).thenReturn(countries);

            List<String> result = userStatisticService.getAvailableCountries();

            assertNotNull(result);
            assertTrue(result.contains("BULGARIA"));
            assertTrue(result.contains("MALAYSIA"));
        }
    }

    @Test
    public void testGetChallengeTypes() {
        List<String> result = userStatisticService.getChallengeTypes();

        assertNotNull(result);
        assertTrue(result.contains(ChallengeType.DAILY.name()));
        assertTrue(result.contains(ChallengeType.THEMED.name()));
    }

    @Test
    void getTop10ByCountry_ReturnsCorrectLeaderboard() {
        try (MockedStatic<CountryEnum> mockedStatic = mockStatic(CountryEnum.class)) {
            mockedStatic.when(CountryEnum::getCountries).thenReturn(Collections.singletonList(CountryEnum.BULGARIA));

            when(userStatisticRepository.findTop10ByCountryOrderByTotalPointsDesc(CountryEnum.BULGARIA))
                    .thenReturn(Collections.singletonList(userStatistic));

            List<LeaderboardsUserByCountryResponse> result = userStatisticService.getTop10ByCountry();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(userStatistic.getUserId(), result.getFirst().getUserId());
            assertEquals(1, result.getFirst().getRank());
        }
    }

    @Test
    public void testGetTop10ByChallengeType() {
        UserStatistic userStatistic = new UserStatistic();
        userStatistic.setUserId(UUID.randomUUID());
        userStatistic.setCountry(CountryEnum.BULGARIA);
        userStatistic.setTotalChallengesWon(5);

        List<UserStatistic> topUsersDaily = Collections.singletonList(userStatistic);
        List<UserStatistic> topUsersThemed = Collections.singletonList(userStatistic);
        List<UserStatistic> topUsersAdmin = Collections.singletonList(userStatistic);
        List<UserStatistic> topUsersAll = Collections.singletonList(userStatistic);

        when(userStatisticRepository.findTop10ByOrderByTotalChallengesWonDailyDesc()).thenReturn(topUsersDaily);
        when(userStatisticRepository.findTop10ByOrderByTotalChallengesWonThemedDesc()).thenReturn(topUsersThemed);
        when(userStatisticRepository.findTop10ByOrderByTotalChallengesWonAdminDesc()).thenReturn(topUsersAdmin);
        when(userStatisticRepository.findTop10ByOrderByTotalChallengesWonDesc()).thenReturn(topUsersAll);

        try (MockedStatic<DtoMapper> mockedDtoMapper = mockStatic(DtoMapper.class)) {
            LeaderboardsUserByChallengeTypeResponse dailyResponse = mock(LeaderboardsUserByChallengeTypeResponse.class);
            LeaderboardsUserByChallengeTypeResponse themedResponse = mock(LeaderboardsUserByChallengeTypeResponse.class);
            LeaderboardsUserByChallengeTypeResponse adminResponse = mock(LeaderboardsUserByChallengeTypeResponse.class);
            LeaderboardsUserByChallengeTypeResponse allResponse = mock(LeaderboardsUserByChallengeTypeResponse.class);

            mockedDtoMapper.when(() -> DtoMapper.mapUserStatisticToLeaderboardsUserByChallengeTypeDailyResponse(userStatistic, ChallengeType.DAILY.name(), 1))
                    .thenReturn(dailyResponse);
            mockedDtoMapper.when(() -> DtoMapper.mapUserStatisticToLeaderboardsUserByChallengeTypeThemedResponse(userStatistic, ChallengeType.THEMED.name(), 1))
                    .thenReturn(themedResponse);
            mockedDtoMapper.when(() -> DtoMapper.mapUserStatisticToLeaderboardsUserByChallengeTypeAdminResponse(userStatistic, ChallengeType.ADMIN.name(), 1))
                    .thenReturn(adminResponse);
            mockedDtoMapper.when(() -> DtoMapper.mapUserStatisticToLeaderboardsUserByChallengeTypeAllResponse(userStatistic, "ALL", 1))
                    .thenReturn(allResponse);

            List<LeaderboardsUserByChallengeTypeResponse> result = userStatisticService.getTop10ByChallengeType();

            assertNotNull(result);
            assertEquals(4, result.size());

            mockedDtoMapper.verify(() -> DtoMapper.mapUserStatisticToLeaderboardsUserByChallengeTypeDailyResponse(userStatistic, ChallengeType.DAILY.name(), 1), times(1));
            mockedDtoMapper.verify(() -> DtoMapper.mapUserStatisticToLeaderboardsUserByChallengeTypeThemedResponse(userStatistic, ChallengeType.THEMED.name(), 1), times(1));
            mockedDtoMapper.verify(() -> DtoMapper.mapUserStatisticToLeaderboardsUserByChallengeTypeAdminResponse(userStatistic, ChallengeType.ADMIN.name(), 1), times(1));
            mockedDtoMapper.verify(() -> DtoMapper.mapUserStatisticToLeaderboardsUserByChallengeTypeAllResponse(userStatistic, "ALL", 1), times(1));
        }
    }

    @Test
    public void testUpdateUserStatistics_ChallengeWonDaily() {
        UUID userId = UUID.randomUUID();
        WinnerRegisterV1 winnerRegisterV1 = new WinnerRegisterV1();
        winnerRegisterV1.setUserId(userId);
        winnerRegisterV1.setPoints(10);
        winnerRegisterV1.setType(ChallengeType.DAILY);

        UserStatistic userStatistic = new UserStatistic();
        userStatistic.setUserId(userId);
        userStatistic.setTotalPoints(100);
        userStatistic.setTotalChallengesWon(0);
        userStatistic.setTotalChallengesWonDaily(0);

        when(userStatisticRepository.findByUserId(userId)).thenReturn(Optional.of(userStatistic));

        userStatisticService.updateUserStatistics(winnerRegisterV1);

        assertEquals(110, userStatistic.getTotalPoints());
        assertEquals(1, userStatistic.getTotalChallengesWon());
        assertEquals(1, userStatistic.getTotalChallengesWonDaily());
    }

    @Test
    public void testUpdateUserStatistics_ChallengeWonThemed() {
        UUID userId = UUID.randomUUID();
        WinnerRegisterV1 winnerRegisterV1 = new WinnerRegisterV1();
        winnerRegisterV1.setUserId(userId);
        winnerRegisterV1.setPoints(10);
        winnerRegisterV1.setType(ChallengeType.THEMED);

        UserStatistic userStatistic = new UserStatistic();
        userStatistic.setUserId(userId);
        userStatistic.setTotalPoints(100);
        userStatistic.setTotalChallengesWon(0);
        userStatistic.setTotalChallengesWonThemed(0);

        when(userStatisticRepository.findByUserId(userId)).thenReturn(Optional.of(userStatistic));

        userStatisticService.updateUserStatistics(winnerRegisterV1);

        assertEquals(110, userStatistic.getTotalPoints());
        assertEquals(1, userStatistic.getTotalChallengesWon());
        assertEquals(1, userStatistic.getTotalChallengesWonThemed());
    }

    @Test
    public void testUpdateUserStatistics_ChallengeWonAdmin() {
        UUID userId = UUID.randomUUID();
        WinnerRegisterV1 winnerRegisterV1 = new WinnerRegisterV1();
        winnerRegisterV1.setUserId(userId);
        winnerRegisterV1.setPoints(10);
        winnerRegisterV1.setType(ChallengeType.ADMIN);

        UserStatistic userStatistic = new UserStatistic();
        userStatistic.setUserId(userId);
        userStatistic.setTotalPoints(100);
        userStatistic.setTotalChallengesWon(0);
        userStatistic.setTotalChallengesWonAdmin(0);

        when(userStatisticRepository.findByUserId(userId)).thenReturn(Optional.of(userStatistic));

        userStatisticService.updateUserStatistics(winnerRegisterV1);

        assertEquals(110, userStatistic.getTotalPoints());
        assertEquals(1, userStatistic.getTotalChallengesWon());
        assertEquals(1, userStatistic.getTotalChallengesWonAdmin());
    }


    @Test
    public void testSaveUserInUserStatistic() {
        UUID userId = UUID.randomUUID();
        UserRegisterV1 userRegisterV1 = new UserRegisterV1();
        userRegisterV1.setUserId(userId);
        userRegisterV1.setCountry(CountryEnum.BULGARIA);

        when(userStatisticRepository.saveAndFlush(any(UserStatistic.class))).thenReturn(new UserStatistic());

        userStatisticService.saveUserInUserStatistic(userRegisterV1);

        verify(userStatisticRepository, times(1)).saveAndFlush(any(UserStatistic.class));
    }

    @Test
    public void testGetUserRankById() {
        UUID userId = UUID.randomUUID();

        UserStatistic userStatistic = new UserStatistic();
        userStatistic.setUserId(userId);
        userStatistic.setTotalPoints(100);
        userStatistic.setUserRank(UserRank.BEGINNER);

        when(userStatisticRepository.findByUserId(userId)).thenReturn(Optional.of(userStatistic));

        UserStatistic result = userStatisticService.getUserRankById(userId);

        assertNotNull(result);

        assertEquals(userStatistic.getUserId(), result.getUserId());
        assertEquals(userStatistic.getTotalPoints(), result.getTotalPoints());
        assertEquals(userStatistic.getUserRank(), result.getUserRank());
    }

    @Test
    public void testGetUserRankResponseById() {
        UUID userId = UUID.randomUUID();

        UserStatistic userStatistic = new UserStatistic();
        userStatistic.setUserId(userId);
        userStatistic.setTotalPoints(100);
        userStatistic.setUserRank(UserRank.BEGINNER);

        when(userStatisticRepository.findByUserId(userId)).thenReturn(Optional.of(userStatistic));

        UserRankResponse result = userStatisticService.getUserRankResponseById(userId);

        assertNotNull(result);

        assertEquals(userStatistic.getUserId(), result.getUserId());
        assertEquals(userStatistic.getUserRank().name(), result.getUserRank().name());
    }

}
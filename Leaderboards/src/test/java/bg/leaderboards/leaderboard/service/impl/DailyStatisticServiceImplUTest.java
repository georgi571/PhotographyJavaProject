package bg.leaderboards.leaderboard.service.impl;

import bg.leaderboards.leaderboard.model.DailyStatistic;
import bg.leaderboards.leaderboard.model.UserStatistic;
import bg.leaderboards.leaderboard.model.CountryEnum;
import bg.leaderboards.leaderboard.repository.DailyStatisticRepository;
import bg.leaderboards.leaderboard.service.UserStatisticService;
import bg.leaderboards.web.dto.LeaderboardsLastThirtyDaysResponse;
import bg.leaderboards.web.dto.WinnerRegisterV1;
import bg.leaderboards.web.mapper.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyStatisticServiceImplUTest {

    @Mock
    private DailyStatisticRepository dailyStatisticRepository;

    @Mock
    private UserStatisticService userStatisticService;

    @InjectMocks
    private DailyStatisticServiceImpl dailyStatisticService;

    private UUID userId;
    private UserStatistic userStatistic;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userStatistic = new UserStatistic();
        userStatistic.setUserId(userId);
        userStatistic.setCountry(CountryEnum.BULGARIA);
    }

    @Test
    void testCleanOldDailyStatistics() {
        LocalDate cutoffDate = LocalDate.now().minusDays(31);
        dailyStatisticService.cleanOldDailyStatistics();
        verify(dailyStatisticRepository, times(1)).deleteByDayBefore(cutoffDate);
    }

    @Test
    void testSaveDailyStatistic() {
        WinnerRegisterV1 winnerRegisterV1 = new WinnerRegisterV1();
        winnerRegisterV1.setUserId(userId);

        when(userStatisticService.getUserRankById(any())).thenReturn(userStatistic);

        DailyStatistic dailyStatistic = new DailyStatistic();

        try (MockedStatic<DtoMapper> mockedMapper = mockStatic(DtoMapper.class)) {
            mockedMapper.when(() -> DtoMapper.mapWinnerRegisterV1ToDailyStatistic(winnerRegisterV1, userStatistic.getCountry()))
                    .thenReturn(dailyStatistic);

            dailyStatisticService.saveDailyStatistic(winnerRegisterV1);

            verify(dailyStatisticRepository, times(1)).saveAndFlush(dailyStatistic);
        }

        dailyStatisticService.saveDailyStatistic(winnerRegisterV1);

        verify(dailyStatisticRepository, times(1)).saveAndFlush(dailyStatistic);
    }

    @Test
    void testGetUserByPointsForLast30Days() {
        LeaderboardsLastThirtyDaysResponse response1 = new LeaderboardsLastThirtyDaysResponse();
        LeaderboardsLastThirtyDaysResponse response2 = new LeaderboardsLastThirtyDaysResponse();
        List<LeaderboardsLastThirtyDaysResponse> mockResponses = List.of(response1, response2);

        when(dailyStatisticRepository.findPointsForLast30Days()).thenReturn(mockResponses);

        List<LeaderboardsLastThirtyDaysResponse> result = dailyStatisticService.getUserByPointsForLast30Days();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getRank());
        assertEquals(2, result.get(1).getRank());
    }
}
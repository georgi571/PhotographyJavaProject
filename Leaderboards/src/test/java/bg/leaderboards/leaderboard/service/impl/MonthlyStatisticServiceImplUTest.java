package bg.leaderboards.leaderboard.service.impl;

import bg.leaderboards.leaderboard.model.MonthlyStatistic;
import bg.leaderboards.leaderboard.model.CountryEnum;
import bg.leaderboards.leaderboard.repository.MonthlyStatisticRepository;
import bg.leaderboards.leaderboard.service.UserStatisticService;
import bg.leaderboards.web.dto.LeaderboardsMonthlyResponse;
import bg.leaderboards.web.dto.WinnerRegisterV1;
import bg.leaderboards.web.mapper.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonthlyStatisticServiceImplUTest {
    @Mock
    private MonthlyStatisticRepository monthlyStatisticRepository;

    @Mock
    private UserStatisticService userStatisticService;

    @InjectMocks
    private MonthlyStatisticServiceImpl monthlyStatisticService;

    private UUID userId;
    private WinnerRegisterV1 winnerRegisterV1;
    LocalDate fixedDate;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        winnerRegisterV1 = new WinnerRegisterV1();
        winnerRegisterV1.setUserId(userId);
        winnerRegisterV1.setPoints(100);
        fixedDate = LocalDate.of(2024, 3, 1);
    }

    @Test
    void testUpdateMonthlyStatistic_NewEntry() {
        when(monthlyStatisticRepository.findByUserIdAndYearAndMonth(any(), anyInt(), any())).thenReturn(Optional.empty());
        when(userStatisticService.getUserRankById(userId)).thenReturn(mockUserStatistic());

        MonthlyStatistic newStatistic = new MonthlyStatistic();
        try (var mockedMapper = mockStatic(DtoMapper.class)) {
            mockedMapper.when(() -> DtoMapper.mapWinnerRegisterV1ToMonthlyStatistic(any(), any(), any()))
                    .thenReturn(newStatistic);

            monthlyStatisticService.updateMonthlyStatistic(winnerRegisterV1);
        }

        ArgumentCaptor<MonthlyStatistic> captor = ArgumentCaptor.forClass(MonthlyStatistic.class);
        verify(monthlyStatisticRepository).saveAndFlush(captor.capture());

        MonthlyStatistic savedStatistic = captor.getValue();
        assertEquals(100, savedStatistic.getPointsEarned());
    }

    @Test
    void testUpdateMonthlyStatistic_ExistingEntry() {
        MonthlyStatistic existingStatistic = new MonthlyStatistic();
        existingStatistic.setPointsEarned(200);

        when(monthlyStatisticRepository.findByUserIdAndYearAndMonth(any(), anyInt(), any()))
                .thenReturn(Optional.of(existingStatistic));

        monthlyStatisticService.updateMonthlyStatistic(winnerRegisterV1);

        ArgumentCaptor<MonthlyStatistic> captor = ArgumentCaptor.forClass(MonthlyStatistic.class);
        verify(monthlyStatisticRepository).saveAndFlush(captor.capture());

        MonthlyStatistic updatedStatistic = captor.getValue();
        assertEquals(300, updatedStatistic.getPointsEarned());
    }

    @Test
    void testGetTopUsersForMonth() {
        LeaderboardsMonthlyResponse response = new LeaderboardsMonthlyResponse();
        response.setUserId(userId);
        response.setPoints(500);

        when(monthlyStatisticRepository.findTopUsersForMonth(anyInt(), any()))
                .thenReturn(Collections.singletonList(response));

        List<LeaderboardsMonthlyResponse> result = monthlyStatisticService.getTopUsersForMonth(2024, Month.JANUARY);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getFirst().getRank());
        assertEquals(500, result.getFirst().getPoints());
    }

    private bg.leaderboards.leaderboard.model.UserStatistic mockUserStatistic() {
        bg.leaderboards.leaderboard.model.UserStatistic userStatistic = new bg.leaderboards.leaderboard.model.UserStatistic();
        userStatistic.setUserId(userId);
        userStatistic.setCountry(CountryEnum.BULGARIA);
        return userStatistic;
    }
}
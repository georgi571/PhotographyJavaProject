package bg.leaderboards.shared.service;

import bg.leaderboards.leaderboard.service.DailyStatisticService;
import bg.leaderboards.leaderboard.service.MonthlyStatisticService;
import bg.leaderboards.leaderboard.service.UserStatisticService;
import bg.leaderboards.web.dto.UserRegisterV1;
import bg.leaderboards.web.dto.WinnerRegisterV1;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerUTest {
    @Mock
    private UserStatisticService userStatisticService;

    @Mock
    private MonthlyStatisticService monthlyStatisticService;

    @Mock
    private DailyStatisticService dailyStatisticService;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;


    @Test
    void testProcessUserRegisterEvent() {
        UserRegisterV1 userRegisterV1 = new UserRegisterV1();

        kafkaConsumer.processUserRegisterEvent(userRegisterV1);

        verify(userStatisticService, times(1)).saveUserInUserStatistic(userRegisterV1);
    }

    @Test
    void testProcessChallengeWinnersEvent() {
        WinnerRegisterV1 winnerRegisterV1 = new WinnerRegisterV1();

        kafkaConsumer.processChallengeWinnersEvent(winnerRegisterV1);

        verify(userStatisticService, times(1)).updateUserStatistics(winnerRegisterV1);
        verify(monthlyStatisticService, times(1)).updateMonthlyStatistic(winnerRegisterV1);
        verify(dailyStatisticService, times(1)).saveDailyStatistic(winnerRegisterV1);
    }

}
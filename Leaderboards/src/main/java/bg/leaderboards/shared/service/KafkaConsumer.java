package bg.leaderboards.shared.service;

import bg.leaderboards.leaderboard.service.DailyStatisticService;
import bg.leaderboards.leaderboard.service.MonthlyStatisticService;
import bg.leaderboards.leaderboard.service.UserStatisticService;
import bg.leaderboards.web.dto.UserRegisterV1;
import bg.leaderboards.web.dto.WinnerRegisterV1;
import jakarta.validation.Valid;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final UserStatisticService userStatisticService;
    private final MonthlyStatisticService monthlyStatisticService;
    private final DailyStatisticService dailyStatisticService;

    public KafkaConsumer(UserStatisticService userStatisticService, MonthlyStatisticService monthlyStatisticService, DailyStatisticService dailyStatisticService) {
        this.userStatisticService = userStatisticService;
        this.monthlyStatisticService = monthlyStatisticService;
        this.dailyStatisticService = dailyStatisticService;
    }

    @KafkaListener(topics = "user-registered-event.v1", groupId = "leaderboards")
    public void processUserRegisterEvent(@Valid UserRegisterV1 userRegisterV1) {
        this.userStatisticService.saveUserInUserStatistic(userRegisterV1);
    }

    @KafkaListener(topics = "challenge-winners-event.v1", groupId = "leaderboards")
    public void processChallengeWinnersEvent(@Valid WinnerRegisterV1 winnerRegisterV1) {
        this.userStatisticService.updateUserStatistics(winnerRegisterV1);
        this.monthlyStatisticService.updateMonthlyStatistic(winnerRegisterV1);
        this.dailyStatisticService.saveDailyStatistic(winnerRegisterV1);
    }
}

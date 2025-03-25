package bg.leaderboards;

import bg.leaderboards.leaderboard.model.CountryEnum;
import bg.leaderboards.leaderboard.repository.UserStatisticRepository;
import bg.leaderboards.leaderboard.service.impl.UserStatisticServiceImpl;
import bg.leaderboards.web.dto.LeaderboardsUserByCountryResponse;
import bg.leaderboards.web.dto.UserRegisterV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class GetTop10ByCountryITest {
    @Autowired
    private UserStatisticServiceImpl userStatisticService;

    @Autowired
    private UserStatisticRepository userStatisticRepository;

    @BeforeEach
    public void setUp() {
        UserRegisterV1 userRegisterV1 = new UserRegisterV1();
        userRegisterV1.setUserId(UUID.randomUUID());
        userRegisterV1.setCountry(CountryEnum.BULGARIA);
        userStatisticService.saveUserInUserStatistic(userRegisterV1);
    }

    @Test
    public void testGetTop10ByCountry_ShouldReturnCorrectResults() {
        List<LeaderboardsUserByCountryResponse> result = userStatisticService.getTop10ByCountry();

        assertEquals(1, result.size());
        assertEquals(1, userStatisticRepository.findAll().size());
    }
}

package bg.challenges;

import bg.challenges.challenge.model.Challenge;
import bg.challenges.challenge.model.ChallengeActivity;
import bg.challenges.challenge.model.ChallengeType;
import bg.challenges.challenge.repository.ChallengeRepository;
import bg.challenges.challenge.service.impl.ChallengeServiceImpl;
import bg.challenges.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class GetAllChallengesITest {
    @Autowired
    private ChallengeServiceImpl challengeService;

    @Autowired
    private ChallengeRepository challengeRepository;

    private Challenge challenge;

    @BeforeEach
    public void setUp() {
        challenge = new Challenge();
        challenge.setTitle("Test Challenge");
        challenge.setDescription("Test Challenge Description");
        challenge.setType(ChallengeType.DAILY);
        challenge.setStartAt(LocalDateTime.now());
        challenge.setEndAt(LocalDateTime.now().plusDays(1));
    }

    @Test
    public void testStartDailyChallengeWithMockedDate() {
        Clock clock = Clock.systemUTC();
        LocalDateTime now = LocalDateTime.now(clock);

        Challenge challenge = new Challenge();
        challenge.setActivity(ChallengeActivity.UPCOMING);
        challenge.setStartAt(now.plusDays(1));
        challenge.setEndAt(now.plusDays(1).withHour(23).withMinute(59).withSecond(59));
        challengeRepository.saveAndFlush(challenge);

        challengeService.startDailyChallenge();

        Challenge updatedChallenge = challengeRepository.findById(challenge.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));

        Assertions.assertEquals(ChallengeActivity.UPCOMING, updatedChallenge.getActivity());

        challenge.setActivity(ChallengeActivity.ACTIVE);
        challengeRepository.saveAndFlush(challenge);

        challengeService.startDailyChallenge();

        updatedChallenge = challengeRepository.findById(challenge.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));

        Assertions.assertEquals(ChallengeActivity.ACTIVE, updatedChallenge.getActivity());
    }
}

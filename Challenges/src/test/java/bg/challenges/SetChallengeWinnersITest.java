package bg.challenges;

import bg.challenges.challenge.model.*;
import bg.challenges.challenge.service.impl.ChallengeServiceImpl;
import bg.challenges.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class SetChallengeWinnersITest {
    @Autowired
    private ChallengeServiceImpl challengeService;

    private Challenge challenge;

    @BeforeEach
    public void setUp() {
        challenge = new Challenge();
        challenge.setTitle("Test Challenge");
        challenge.setDescription("Test Challenge Description");
        challenge.setType(ChallengeType.DAILY);
        challenge.setId(UUID.randomUUID());
        challenge.setStartAt(LocalDateTime.now());
        challenge.setEndAt(LocalDateTime.now().plusDays(1));
    }

    @Test
    public void testSetChallengeWinners_ChallengeNotFound() {
        UUID nonExistentChallengeId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () -> {
            challengeService.setChallengeWinners(nonExistentChallengeId);
        });
    }
}

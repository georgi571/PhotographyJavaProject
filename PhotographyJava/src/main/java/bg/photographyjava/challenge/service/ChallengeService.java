package bg.photographyjava.challenge.service;

import bg.photographyjava.web.dto.ChallengeDTO;
import bg.photographyjava.web.dto.ChallengeDetailsDTO;

import java.util.List;
import java.util.UUID;

public interface ChallengeService {
    void startDailyChallenge();

    void checkAndSetWinners();

    void setChallengeWinners(UUID challengeId);

    List<ChallengeDTO> getAllChallenges();

    ChallengeDetailsDTO getChallengeDetails(UUID id);
}

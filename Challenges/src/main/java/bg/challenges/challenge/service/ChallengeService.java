package bg.challenges.challenge.service;

import bg.challenges.challenge.model.Challenge;
import bg.challenges.challenge.model.ChallengeType;
import bg.challenges.web.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ChallengeService {
    void startDailyChallenge();

    void setChallengeWinners(UUID challengeId);

    List<ChallengeResponse> getAllChallenges();

    ChallengeDetailsResponse getChallengeDetails(UUID id, UUID userId);

    PictureResponse savePictureForChallenge(UUID challengeId, MultipartFile file, String caption, String story, UUID userId) throws IOException;

    List<Challenge> findByType(ChallengeType challengeType);

    ChallengeResponse createChallenge(CreateChallengeRequest createChallengeRequest);

    ChallengeResponse editChallenge(UUID id, EditChallengeRequest editChallengeRequest);

    void deleteChallenge(UUID id);
}

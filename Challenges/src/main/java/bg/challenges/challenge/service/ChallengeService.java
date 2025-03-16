package bg.challenges.challenge.service;

import bg.challenges.challenge.model.Challenge;
import bg.challenges.challenge.property.enums.ChallengeType;
import bg.challenges.web.dto.*;

import java.util.List;
import java.util.UUID;

public interface ChallengeService {
    void startDailyChallenge();

    void setChallengeWinners(UUID challengeId);

    List<ChallengeResponse> getAllChallenges();

    ChallengeDetailsResponse getChallengeDetails(UUID id, UUID userId);

    void savePictureForChallenge(UUID challengeId, String pictureFilePath, String caption, String story, UUID userId);

    PictureToggleResponse toggleLikePicture(UUID challengeId, UUID pictureId, UUID userId);

    CommentResponse addComment(UUID challengeId, UUID pictureId, String text, UUID userId);

    void deletePicture(UUID challengeId, UUID pictureId, UUID userId);

    void deleteComment(UUID challengeId, UUID pictureId, UUID commentId, UUID userId);

    List<Challenge> findByType(ChallengeType challengeType);

    ChallengeResponse createChallenge(CreateChallengeRequest createChallengeRequest);

    ChallengeResponse editChallenge(UUID id, EditChallengeRequest editChallengeRequest);

    void deleteChallenge(UUID id);
}

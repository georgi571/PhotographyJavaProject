package bg.photographyjava.challenge.service;

import bg.photographyjava.challenge.model.Challenge;
import bg.photographyjava.challenge.property.enums.ChallengeType;
import bg.photographyjava.web.dto.*;

import java.util.List;
import java.util.UUID;

public interface ChallengeService {
    void startDailyChallenge();

    void checkAndSetWinners();

    void setChallengeWinners(UUID challengeId);

    List<ChallengeResponse> getAllChallenges();

    ChallengeDetailsResponse getChallengeDetails(UUID id, String username);

    boolean savePictureForChallenge(UUID challengeId, String pictureFilePath, String caption, String story, String username);

    PictureToggleDTO toggleLikePicture(UUID challengeId, UUID pictureId, String username);

    CommentResponseDTO addComment(UUID challengeId, UUID pictureId, String text, String username);

    String reportPicture(UUID challengeId, UUID pictureId, String username, String reportReason);

    String reportComment(UUID challengeId, UUID pictureId, UUID commentId, String username, String reportReason);

    String deletePicture(UUID challengeId, UUID pictureId, String username);

    String deleteComment(UUID challengeId, UUID pictureId, UUID commentId, String username);

    List<Challenge> findByType(ChallengeType challengeType);

    ChallengeResponse createChallenge(CreateEventRequest createEventRequest, String username);

    ChallengeResponse editChallenge(UUID id, EditEventRequest editEventRequest, String name);

    void deleteChallenge(UUID id, String name);
}

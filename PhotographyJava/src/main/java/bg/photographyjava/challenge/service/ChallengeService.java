package bg.photographyjava.challenge.service;

import bg.photographyjava.web.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ChallengeService {
    void startDailyChallenge();

    void checkAndSetWinners();

    void setChallengeWinners(UUID challengeId);

    List<ChallengeDTO> getAllChallenges();

    ChallengeDetailsDTO getChallengeDetails(UUID id, String username);

    boolean savePictureForChallenge(UUID challengeId, String pictureFilePath, String caption, String story, String username);

    PictureToggleDTO toggleLikePicture(UUID challengeId, UUID pictureId, String username);

    CommentResponseDTO addComment(UUID challengeId, UUID pictureId, String text, String username);

    String reportPicture(UUID challengeId, UUID pictureId, String username, String reportReason);

    String reportComment(UUID challengeId, UUID pictureId, UUID commentId, String username, String reportReason);

    String deletePicture(UUID challengeId, UUID pictureId, String username);

    String deleteComment(UUID challengeId, UUID pictureId, UUID commentId, String username);
}

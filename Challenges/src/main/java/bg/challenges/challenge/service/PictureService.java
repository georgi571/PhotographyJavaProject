package bg.challenges.challenge.service;

import bg.challenges.challenge.model.Picture;
import bg.challenges.web.dto.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface PictureService {
    List<Picture> getWinnersPicture(UUID challengeId);

    Picture getPictureById(UUID pictureId);

    void savePicture(Picture picture);

    PictureToggleResponse toggleLikePicture(UUID pictureId, UUID userId);

    CommentResponse addComment(UUID pictureId, String text, UUID userId);

    void deletePicture(UUID pictureId, UUID userId, Authentication authentication);

    PictureReportResponse getReportedPicture(UUID id);

    List<PictureResponse> getAllPicturesByUser(UUID id, UUID userId);
}

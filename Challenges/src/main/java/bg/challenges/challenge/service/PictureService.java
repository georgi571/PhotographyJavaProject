package bg.challenges.challenge.service;

import bg.challenges.challenge.model.Picture;

import java.util.List;
import java.util.UUID;

public interface PictureService {
    List<Picture> getWinnersPicture(UUID challengeId);

    Picture getPictureById(UUID pictureId);

    void savePicture(Picture picture);
}

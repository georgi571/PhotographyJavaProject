package bg.challenges.challenge.service.impl;

import bg.challenges.challenge.model.Picture;
import bg.challenges.challenge.repository.PictureRepository;
import bg.challenges.challenge.service.PictureService;
import bg.challenges.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class PictureServiceImpl implements PictureService {

    private final PictureRepository pictureRepository;

    public PictureServiceImpl(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @Override
    public List<Picture> getWinnersPicture(UUID challengeId) {
        List<Picture> pictures = pictureRepository.findByChallengeId(challengeId);
        return pictures.stream()
                .sorted(Comparator.comparingLong(Picture::getLikes).reversed())
                .toList();
    }

    @Override
    public Picture getPictureById(UUID pictureId) {
        return this.pictureRepository.findById(pictureId)
                .orElseThrow(() -> new ResourceNotFoundException("Picture not found with id: " + pictureId));
    }

    @Override
    public void savePicture(Picture picture) {
        this.pictureRepository.saveAndFlush(picture);
    }

}

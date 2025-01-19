package bg.photographyjava.challenge.service.impl;

import bg.photographyjava.challenge.model.Picture;
import bg.photographyjava.challenge.repository.PictureRepository;
import bg.photographyjava.challenge.service.PictureService;
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
}

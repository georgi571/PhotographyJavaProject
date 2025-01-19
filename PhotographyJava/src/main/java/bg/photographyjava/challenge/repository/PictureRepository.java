package bg.photographyjava.challenge.repository;

import bg.photographyjava.challenge.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PictureRepository extends JpaRepository<Picture, UUID> {
    List<Picture> findByChallengeId(UUID challengeId);
}

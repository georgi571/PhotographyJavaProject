package bg.photographyjava.challenge.repository;

import bg.photographyjava.challenge.model.Challenge;
import bg.photographyjava.challenge.property.enums.ChallengeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    List<Challenge> findByEndAtBeforeAndWinnersIsNull(LocalDateTime now);

    List<Challenge> findByType(ChallengeType challengeType);
}

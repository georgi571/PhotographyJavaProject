package bg.challenges.challenge.repository;

import bg.challenges.challenge.model.Challenge;
import bg.challenges.challenge.model.ChallengeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    List<Challenge> findByStartAt(LocalDateTime now);

    List<Challenge> findByEndAtBeforeAndWinnersIsNull(LocalDateTime now);

    List<Challenge> findByType(ChallengeType challengeType);
}

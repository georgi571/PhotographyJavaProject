package bg.challenges.challenge.repository;

import bg.challenges.challenge.model.Winner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WinnerRepository extends JpaRepository<Winner, UUID> {
}

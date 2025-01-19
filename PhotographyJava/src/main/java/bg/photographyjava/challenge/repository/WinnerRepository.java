package bg.photographyjava.challenge.repository;

import bg.photographyjava.challenge.model.Winner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WinnerRepository extends JpaRepository<Winner, UUID> {
}

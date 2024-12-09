package bg.photographyjava.repository;

import bg.photographyjava.model.entity.Rank;
import bg.photographyjava.model.enums.UserRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RankRepository extends JpaRepository<Rank, UUID> {
    Rank findByRank(UserRank userRank);
}

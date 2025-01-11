package bg.photographyjava.user.repository;

import bg.photographyjava.user.model.Rank;
import bg.photographyjava.user.property.enums.UserRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RankRepository extends JpaRepository<Rank, UUID> {
    Rank findByRank(UserRank userRank);
}

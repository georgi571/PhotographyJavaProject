package bg.photographyjava.repository;

import bg.photographyjava.model.entity.Role;
import bg.photographyjava.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role findByRole(UserRole userRole);
}

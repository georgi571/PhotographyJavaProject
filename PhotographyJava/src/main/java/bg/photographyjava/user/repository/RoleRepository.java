package bg.photographyjava.user.repository;

import bg.photographyjava.user.model.Role;
import bg.photographyjava.user.property.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role findByRole(UserRole userRole);
}

package bg.photographyjava.user.service.impl;

import bg.photographyjava.user.model.Role;
import bg.photographyjava.user.property.enums.UserRole;
import bg.photographyjava.user.repository.RoleRepository;
import bg.photographyjava.user.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void seedRoles() {
        if (this.roleRepository.count() == 0) {
            for (UserRole role : UserRole.getRoles()) {
                this.roleRepository.saveAndFlush(new Role(role));
            }
        }
    }
}

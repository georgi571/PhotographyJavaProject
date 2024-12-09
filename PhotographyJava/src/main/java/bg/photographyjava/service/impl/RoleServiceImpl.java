package bg.photographyjava.service.impl;

import bg.photographyjava.model.entity.Role;
import bg.photographyjava.model.enums.UserRole;
import bg.photographyjava.repository.RoleRepository;
import bg.photographyjava.service.RoleService;
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

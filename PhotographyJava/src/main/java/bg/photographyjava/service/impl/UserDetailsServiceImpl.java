package bg.photographyjava.service.impl;

import bg.photographyjava.model.entity.UserEntity;
import bg.photographyjava.model.entity.UserPrincipal;
import bg.photographyjava.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = this.userRepository.findByUsername(username).get();

        if (user == null) {
            System.out.printf("User %s not found.\n", username);
            throw new UsernameNotFoundException("User not found.");
        }

        return new UserPrincipal(user);
    }

    private UserDetails map(UserEntity userEntity) {
        return User
                .withUsername(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(List.of())
                .disabled(false)
                .build();
    }
}

package bg.photographyjava.user.service.impl;

import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.user.model.UserPrincipal;
import bg.photographyjava.user.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getRole());

        return new UserPrincipal(user);
    }

    private UserDetails map(UserEntity userEntity) {
        return User
                .withUsername(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().getRole().name()))
                .disabled(false)
                .build();
    }
}

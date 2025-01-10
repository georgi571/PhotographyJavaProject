package bg.photographyjava.service.impl;

import bg.photographyjava.model.dto.UserLoginDTO;
import bg.photographyjava.model.entity.UserEntity;
import bg.photographyjava.model.dto.UserRegisterDTO;
import bg.photographyjava.model.enums.CountryEnum;
import bg.photographyjava.model.enums.GenderEnum;
import bg.photographyjava.model.enums.UserRank;
import bg.photographyjava.model.enums.UserRole;
import bg.photographyjava.repository.CountryRepository;
import bg.photographyjava.repository.RankRepository;
import bg.photographyjava.repository.RoleRepository;
import bg.photographyjava.repository.UserRepository;
import bg.photographyjava.service.UserService;
import bg.photographyjava.service.filter.JWTService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RankRepository userRankRepository;
    private final RoleRepository userRoleRepository;
    private final CountryRepository countryRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, RankRepository userRankRepository, RoleRepository userRoleRepository, CountryRepository countryRepository, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRankRepository = userRankRepository;
        this.userRoleRepository = userRoleRepository;
        this.countryRepository = countryRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public void seedUsers() {
        if (this.userRepository.count() == 0) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setAge(100);
            admin.setEmail("admin@gmail.com");
            admin.setCountry(this.countryRepository.findByName(CountryEnum.BULGARIA));
            admin.setCity("Blagoevgrad");
            admin.setGender(GenderEnum.MALE);
            admin.setRank(this.userRankRepository.findByRank(UserRank.MASTER));
            admin.setRole(this.userRoleRepository.findByRole(UserRole.ADMIN));
            admin.setApproved(true);
            this.userRepository.saveAndFlush(admin);
        }
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> getUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public void registerUser(UserRegisterDTO userRegisterDTO) {
        UserEntity user = this.modelMapper.map(userRegisterDTO, UserEntity.class);
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setCountry(this.countryRepository.findByName(CountryEnum.fromString(userRegisterDTO.getCountry())));
        user.setGender(GenderEnum.fromString(userRegisterDTO.getGender()));
        user.setRank(this.userRankRepository.findByRank(UserRank.BEGINNER));
        user.setRole(this.userRoleRepository.findByRole(UserRole.USER));
        user.setApproved(false);
        this.userRepository.saveAndFlush(user);
    }

    @Override
    public String verify(UserLoginDTO userLoginDTO) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword()));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(userLoginDTO.getUsername());
        }
        return "Failed";
    }


}

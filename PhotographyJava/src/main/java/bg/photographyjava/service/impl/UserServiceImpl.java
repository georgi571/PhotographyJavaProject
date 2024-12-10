package bg.photographyjava.service.impl;

import bg.photographyjava.model.entity.User;
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
import org.modelmapper.ModelMapper;
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

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, RankRepository userRankRepository, RoleRepository userRoleRepository, CountryRepository countryRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRankRepository = userRankRepository;
        this.userRoleRepository = userRoleRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public void seedUsers() {
        if (this.userRepository.count() == 0) {
            User admin = new User();
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
    public Optional<User> getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public void registerUser(UserRegisterDTO userRegisterDTO) {
        User user = this.modelMapper.map(userRegisterDTO, User.class);
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setCountry(this.countryRepository.findByName(CountryEnum.fromString(userRegisterDTO.getCountry())));
        user.setGender(GenderEnum.fromString(userRegisterDTO.getGender()));
        user.setRank(this.userRankRepository.findByRank(UserRank.BEGINNER));
        user.setRole(this.userRoleRepository.findByRole(UserRole.USER));
        user.setApproved(false);
        this.userRepository.saveAndFlush(user);
    }


}

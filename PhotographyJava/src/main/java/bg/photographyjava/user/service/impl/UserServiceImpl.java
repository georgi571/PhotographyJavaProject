package bg.photographyjava.user.service.impl;

import bg.photographyjava.user.model.Role;
import bg.photographyjava.web.dto.*;
import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.user.property.enums.CountryEnum;
import bg.photographyjava.user.property.enums.GenderEnum;
import bg.photographyjava.user.property.enums.UserRank;
import bg.photographyjava.user.property.enums.UserRole;
import bg.photographyjava.user.repository.CountryRepository;
import bg.photographyjava.user.repository.RankRepository;
import bg.photographyjava.user.repository.RoleRepository;
import bg.photographyjava.user.repository.UserRepository;
import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.filter.JWTService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
            admin.setRealName("Admin Admin");
            admin.setPoints(2500);
            admin.setBanned(false);
            admin.setProfilePicturePath("/img/male-profile-picture.avif");
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
        user.setRealName("Anonymous");
        user.setPoints(0);
        user.setBanned(false);
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

    @Override
    public UserProfileDTO getProfileDetails(String username) {
        UserEntity user = this.getUserByUsername(username).get();
        UserProfileDTO userProfileDTO = this.modelMapper.map(user, UserProfileDTO.class);
        userProfileDTO.setCountry(user.getCountry().getName().getCountryName());
        userProfileDTO.setRank(user.getRank().getRank().toString());
        userProfileDTO.setPoints(user.getPoints());

        return userProfileDTO;
    }

    @Override
    public UserEditProfileDTO getProfileEditDetails(String username) {
        UserEntity user = this.getUserByUsername(username).get();

        return this.modelMapper.map(user, UserEditProfileDTO.class);
    }

    @Override
    public void editUserDetails(String username, UserEditProfileDTO userEditProfileDTO) {
        UserEntity user = this.getUserByUsername(username).get();
        user.setRealName(userEditProfileDTO.getRealName());
        user.setCity(userEditProfileDTO.getCity());
        user.setAge(userEditProfileDTO.getAge());

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public UserChangeUsernameDTO getUserUsernameDetails(String username) {
        UserEntity user = this.getUserByUsername(username).get();
        UserChangeUsernameDTO userChangeUsernameDTO = new UserChangeUsernameDTO();
        userChangeUsernameDTO.setOldUsername(user.getUsername());

        return userChangeUsernameDTO;
    }

    @Override
    public UserChangeEmailDTO getUserEmailDetails(String username) {
        UserEntity user = this.getUserByUsername(username).get();

        UserChangeEmailDTO userChangeEmailDTO = new UserChangeEmailDTO();
        userChangeEmailDTO.setOldEmail(user.getEmail());
        return userChangeEmailDTO;
    }

    @Override
    public void editUserUsernameDetails(String username, UserChangeUsernameDTO userChangeUsernameDTO) {
        UserEntity user = this.getUserByUsername(username).get();
        user.setUsername(userChangeUsernameDTO.getNewUsername());

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public void editUserEmailDetails(String username, UserChangeEmailDTO userChangeEmailDTO) {
        UserEntity user = this.getUserByUsername(username).get();
        user.setEmail(userChangeEmailDTO.getNewEmail());

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public void updatePassword(String username, String encodedNewPassword) {
        UserEntity user = this.getUserByUsername(username).get();
        user.setPassword(encodedNewPassword);

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public List<ChangeRoleUserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(user -> new ChangeRoleUserDTO(
                user.getId(),
                user.getUsername(),
                user.getRole().getRole().name()
        )).toList();
    }

    @Override
    public void updateUserRole(UUID userId, String roleToChange, String username) {
        UserEntity admin = this.getUserByUsername(username).get();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserRole userRole = Enum.valueOf(UserRole.class, roleToChange);
        Role role = this.userRoleRepository.findByRole(userRole);

        user.setRole(role);
        userRepository.saveAndFlush(user);
    }

    @Override
    public List<BanUserDTO> getAllUsersForBan() {
        return userRepository.findAll().stream().map(user -> new BanUserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isBanned()
        )).toList();
    }

    @Override
    public void banUserAction(UUID id, String action, String username) {
        UserEntity user = this.userRepository.findById(id).get();
        user.setBanned(action.equals("ban"));

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public BanUserDTO getUserForBan(UUID id) {
        UserEntity user = this.userRepository.findById(id).get();

        return this.modelMapper.map(user, BanUserDTO.class);
    }


}

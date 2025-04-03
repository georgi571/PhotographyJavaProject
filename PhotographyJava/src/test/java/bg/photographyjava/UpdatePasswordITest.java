package bg.photographyjava;

import bg.photographyjava.exception.InvalidPasswordException;
import bg.photographyjava.exception.UserNotFoundException;
import bg.photographyjava.user.model.Country;
import bg.photographyjava.user.model.Role;
import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.user.property.enums.CountryEnum;
import bg.photographyjava.user.property.enums.GenderEnum;
import bg.photographyjava.user.property.enums.UserRole;
import bg.photographyjava.user.repository.CountryRepository;
import bg.photographyjava.user.repository.RoleRepository;
import bg.photographyjava.user.repository.UserRepository;
import bg.photographyjava.user.service.impl.UserServiceImpl;
import bg.photographyjava.web.dto.UserChangePasswordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class UpdatePasswordITest {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        Country country = new Country();
        country.setName(CountryEnum.BULGARIA);
        countryRepository.save(country);

        Role role = new Role();
        role.setRole(UserRole.USER);
        roleRepository.save(role);

        user = new UserEntity();
        user.setUsername("testUser1");
        user.setRealName("Test User 1");
        user.setEmail("test1@abv.bg");
        user.setCity("Blagoevgrad");

        user.setPassword(passwordEncoder.encode("oldPassword"));
        user.setGender(GenderEnum.MALE);
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setCountry(country);
        user.setRole(role);
        user.setFriends(new HashSet<>());
        user.setReceiveFriendRequest(new HashSet<>());
        user.setSendFriendRequest(new HashSet<>());
        user.setPermissions(new HashSet<>());
        userRepository.save(user);
    }

    @Test
    void testUpdatePassword_Success() {
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");

        userService.updatePassword("testUser1", request);

        UserEntity updatedUser = userRepository.findByUsername("testUser1").orElseThrow();
        assertThat(passwordEncoder.matches("newPassword", updatedUser.getPassword())).isTrue();
    }

    @Test
    void testUpdatePassword_InvalidOldPassword() {
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setOldPassword("wrongOldPassword");
        request.setNewPassword("newPassword");

        assertThatThrownBy(() -> userService.updatePassword("testUser1", request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Invalid current password");
    }

    @Test
    void testUpdatePassword_UserNotFound() {
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");

        assertThatThrownBy(() -> userService.updatePassword("nonExistingUser", request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with username nonExistingUser not found");
    }
}

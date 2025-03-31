package bg.photographyjava;

import bg.photographyjava.exception.InvalidPasswordException;
import bg.photographyjava.exception.OldUsernameMismatchException;
import bg.photographyjava.exception.UserNotFoundException;
import bg.photographyjava.user.model.Country;
import bg.photographyjava.user.model.Role;
import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.user.property.enums.CountryEnum;
import bg.photographyjava.user.property.enums.GenderEnum;
import bg.photographyjava.user.property.enums.UserPermission;
import bg.photographyjava.user.property.enums.UserRole;
import bg.photographyjava.user.repository.CountryRepository;
import bg.photographyjava.user.repository.RoleRepository;
import bg.photographyjava.user.repository.UserRepository;
import bg.photographyjava.user.service.impl.UserServiceImpl;
import bg.photographyjava.web.dto.AdminPermissionsResponse;
import bg.photographyjava.web.dto.AdminPermissionsUpdateRequest;
import bg.photographyjava.web.dto.UserChangeUsernameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class EditUserUsernameDetailsITest {

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

    private UserEntity admin;

    @BeforeEach
    void setUp() {
        Country country = new Country();
        country.setName(CountryEnum.BULGARIA);
        countryRepository.save(country);

        Role role = new Role();
        role.setRole(UserRole.USER);
        roleRepository.save(role);

        admin = new UserEntity();
        admin.setUsername("testUser1");
        admin.setRealName("Test User 1");
        admin.setEmail("test1@abv.bg");
        admin.setCity("Blagoevgrad");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setGender(GenderEnum.MALE);
        admin.setBirthDate(LocalDate.of(2000, 1, 1));
        admin.setCountry(country);
        admin.setRole(role);
        admin.setFriends(new HashSet<>());
        admin.setReceiveFriendRequest(new HashSet<>());
        admin.setSendFriendRequest(new HashSet<>());
        admin.setPermissions(new HashSet<>());
        userRepository.save(admin);
    }

    @Test
    void testEditUserUsernameDetails_Success() {
        UserChangeUsernameRequest request = new UserChangeUsernameRequest();
        request.setOldUsername("testUser1");
        request.setPassword("password");
        request.setNewUsername("newTestUser");

        userService.editUserUsernameDetails("testUser1", request);

        UserEntity updatedUser = userRepository.findByUsername("newTestUser").orElseThrow();
        assertThat(updatedUser.getUsername()).isEqualTo("newTestUser");
    }

    @Test
    void testEditUserUsernameDetails_InvalidPassword() {
        UserChangeUsernameRequest request = new UserChangeUsernameRequest();
        request.setOldUsername("testUser1");
        request.setPassword("wrongPassword");
        request.setNewUsername("newTestUser");

        assertThatThrownBy(() -> userService.editUserUsernameDetails("testUser1", request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Invalid current password");
    }

    @Test
    void testEditUserUsernameDetails_OldUsernameMismatch() {
        UserChangeUsernameRequest request = new UserChangeUsernameRequest();
        request.setOldUsername("incorrectOldUsername");
        request.setPassword("password");
        request.setNewUsername("newTestUser");

        assertThatThrownBy(() -> userService.editUserUsernameDetails("testUser1", request))
                .isInstanceOf(OldUsernameMismatchException.class)
                .hasMessageContaining("Old username does not match the authenticated username.");
    }

    @Test
    void testEditUserUsernameDetails_UserNotFound() {
        UserChangeUsernameRequest request = new UserChangeUsernameRequest();
        request.setOldUsername("nonExistentUser");
        request.setPassword("oldPassword");
        request.setNewUsername("newTestUser");

        assertThatThrownBy(() -> userService.editUserUsernameDetails("nonExistentUser", request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with username nonExistentUser not found");
    }
}

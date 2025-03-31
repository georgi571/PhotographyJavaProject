package bg.photographyjava.web.mapper;

import bg.photographyjava.user.model.Country;
import bg.photographyjava.user.model.Role;
import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.user.property.enums.CountryEnum;
import bg.photographyjava.user.property.enums.GenderEnum;
import bg.photographyjava.user.property.enums.UserPermission;
import bg.photographyjava.user.property.enums.UserRole;
import bg.photographyjava.web.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DtoMapperUTest {

    @Test
    void testMapUserEntityToUserEditProfileResponse() {
        UserEntity user = new UserEntity();
        user.setRealName("John Doe");
        user.setCity("New York");
        user.setBirthDate(LocalDate.of(1990, 5, 10));

        UserEditProfileResponse response = DtoMapper.mapUserEntityToUserEditProfileResponse(user);

        assertNotNull(response);
        assertEquals("John Doe", response.getRealName());
        assertEquals("New York", response.getCity());
        assertEquals(LocalDate.of(1990, 5, 10), response.getBirthDate());
    }

    @Test
    void testMapUserEditProfileRequestToUserEntity() {
        UserEntity user = new UserEntity();
        UserEditProfileRequest request = new UserEditProfileRequest();
        request.setRealName("Jane Doe");
        request.setCity("Los Angeles");
        request.setBirthDate(LocalDate.of(1985, 7, 25));

        DtoMapper.mapUserEditProfileRequestToUserEntity(user, request);

        assertEquals("Jane Doe", user.getRealName());
        assertEquals("Los Angeles", user.getCity());
        assertEquals(LocalDate.of(1985, 7, 25), user.getBirthDate());
    }

    @Test
    void testMapUserEntityToUserChangeUsernameResponse() {
        UserEntity user = new UserEntity();
        user.setUsername("oldUsername");

        UserChangeUsernameResponse response = DtoMapper.mapUserEntityToUserChangeUsernameResponse(user);

        assertNotNull(response);
        assertEquals("oldUsername", response.getOldUsername());
    }

    @Test
    void testMapUserChangeEmailRequestToUserEntity() {
        UserEntity user = new UserEntity();
        UserChangeEmailRequest request = new UserChangeEmailRequest();
        request.setNewEmail("newEmail@example.com");

        DtoMapper.mapUserChangeEmailRequestToUserEntity(user, request);

        assertEquals("newEmail@example.com", user.getEmail());
    }

    @Test
    void testMapUserEntityToAdminPermissionsResponse() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername("adminUser");
        user.setPermissions(Set.of(UserPermission.BAN_USERS));

        AdminPermissionsResponse response = DtoMapper.mapUserEntityToAdminPermissionsResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getUsername(), response.getUsername());
        assertTrue(response.getPermissions().contains(UserPermission.BAN_USERS));
    }

    @Test
    void testMapUserEntityToUserProfileResponse() {
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setProfilePicturePath("profilePic.jpg");
        user.setCity("Paris");
        Country country = new Country();
        country.setName(CountryEnum.FRANCE);
        user.setCountry(country);
        user.setGender(GenderEnum.MALE);
        user.setRealName("Test User");
        user.setBirthDate(LocalDate.of(1990, 12, 10));

        UserProfileResponse response = DtoMapper.mapUserEntityToUserProfileResponse(user);

        assertNotNull(response);
        assertEquals("testUser", response.getUsername());
        assertEquals("profilePic.jpg", response.getProfilePicturePath());
        assertEquals("Paris", response.getCity());
        assertEquals("France", response.getCountry());
        assertEquals(34, response.getAge());
        assertEquals("Male", response.getGender());
        assertEquals("Test User", response.getRealName());
    }

    @Test
    void testMapUserEntityToUserRegisterV1() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        Country country = new Country();
        country.setName(CountryEnum.ALBANIA);
        user.setCountry(country);

        UserRegisterV1 response = DtoMapper.mapUserEntityToUserRegisterV1(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals("Albania", response.getCountry().getCountryName());
    }

    @Test
    void testMapUserChangeUsernameRequestToUserEntity() {
        UserEntity user = new UserEntity();
        UserChangeUsernameRequest request = new UserChangeUsernameRequest();
        request.setNewUsername("newUsername");

        DtoMapper.mapUserChangeUsernameRequestToUserEntity(user, request);

        assertEquals("newUsername", user.getUsername());
    }

    @Test
    void testMapUserEntityToUserChangeEmailResponse() {
        UserEntity user = new UserEntity();
        user.setEmail("oldEmail@example.com");

        UserChangeEmailResponse response = DtoMapper.mapUserEntityToUserChangeEmailResponse(user);

        assertNotNull(response);
        assertEquals("oldEmail@example.com", response.getOldEmail());
    }

    @Test
    void testMapUserEntityToChangeRoleUserResponse() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername("testUser");
        Role role = new Role();
        role.setRole(UserRole.ADMIN);
        user.setRole(role);

        ChangeRoleUserResponse response = DtoMapper.mapUserEntityToChangeRoleUserResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void testMapUserEntityToBanUserResponse() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername("bannedUser");
        user.setEmail("bannedUser@example.com");
        user.setBanned(true);
        user.setReasonForBan("Spamming");

        BanUserResponse response = DtoMapper.mapUserEntityToBanUserResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
        assertTrue(response.isBanned());
        assertEquals("Spamming", response.getReasonForBan());
    }


    @Test
    void testMapUserEntityToApproveUsersResponse() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername("approveUser");
        user.setEmail("approveUser@example.com");

        ApproveUsersResponse response = DtoMapper.mapUserEntityToApproveUsersResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
    }


    @Test
    void testMapUserEntityToModeratorPermissionsResponse() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername("moderatorUser");
        user.setPermissions(Set.of(UserPermission.DELETE_MESSAGE));

        ModeratorPermissionsResponse response = DtoMapper.mapUserEntityToModeratorPermissionsResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getUsername(), response.getUsername());
        assertTrue(response.getPermissions().contains(UserPermission.DELETE_MESSAGE));
    }


    @Test
    void testMapUserEntityToFriendsResponse() {
        UserEntity user = new UserEntity();
        user.setUsername("friendUser");
        user.setProfilePicturePath("profilePic.jpg");

        FriendsResponse response = DtoMapper.mapUserEntityToFriendsResponse(user);

        assertNotNull(response);
        assertEquals("friendUser", response.getUsername());
        assertEquals("profilePic.jpg", response.getProfilePicturePath());
    }


    @Test
    void testMapUserEntityToFollowersResponse() {
        UserEntity user = new UserEntity();
        user.setUsername("followerUser");
        user.setProfilePicturePath("followerPic.jpg");

        FollowersResponse response = DtoMapper.mapUserEntityToFollowersResponse(user);

        assertNotNull(response);
        assertEquals("followerUser", response.getUsername());
        assertEquals("followerPic.jpg", response.getProfilePicturePath());
    }


    @Test
    void testMapUserEntityToBlockedUserResponse() {
        UserEntity user = new UserEntity();
        user.setUsername("blockedUser");
        user.setProfilePicturePath("blockedPic.jpg");

        BlockedUserResponse response = DtoMapper.mapUserEntityToBlockedUserResponse(user);

        assertNotNull(response);
        assertEquals("blockedUser", response.getUsername());
        assertEquals("blockedPic.jpg", response.getProfilePicturePath());
    }


    @Test
    void testMapUserEntityToContactUserResponse() {
        UserEntity user = new UserEntity();
        user.setEmail("contact@example.com");
        user.setRealName("John Doe");

        ContactUserResponse response = DtoMapper.mapUserEntityToContactUserResponse(user);

        assertNotNull(response);
        assertEquals("contact@example.com", response.getEmail());
        assertEquals("John Doe", response.getRealName());
    }


    @Test
    void testMapUserEntityToUserInformationForPictureResponse() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername("userForPicture");
        user.setProfilePicturePath("picPath.jpg");

        UserInformationForPictureResponse response = DtoMapper.mapUserEntityToUserInformationForPictureResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals("picPath.jpg", response.getProfilePicturePath());
    }


    @Test
    void testMapUserRegisterRequestToUserEntity() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("newUser");
        request.setEmail("newUser@example.com");
        request.setGender("MALE");
        request.setCity("Blagoevgrad");
        request.setBirthDate(LocalDate.of(2000, 1, 1));

        UserEntity user = DtoMapper.mapUserRegisterRequestToUserEntity(request);

        assertNotNull(user);
        assertEquals("newUser", user.getUsername());
        assertEquals("newUser@example.com", user.getEmail());
        assertEquals("Male", user.getGender().getGenderType());
        assertEquals("Blagoevgrad", user.getCity());
        assertEquals(LocalDate.of(2000, 1, 1), user.getBirthDate());
    }











}
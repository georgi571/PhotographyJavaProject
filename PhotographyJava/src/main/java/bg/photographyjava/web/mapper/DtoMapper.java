package bg.photographyjava.web.mapper;

import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.user.property.enums.CountryEnum;
import bg.photographyjava.user.property.enums.GenderEnum;
import bg.photographyjava.user.property.enums.UserRank;
import bg.photographyjava.user.property.enums.UserRole;
import bg.photographyjava.web.dto.*;

import java.time.LocalDate;
import java.time.Period;

public class DtoMapper {

    public static UserEditProfileResponse mapUserEntityToUserEditProfileResponse (UserEntity user) {

        UserEditProfileResponse userEditProfileResponse = new UserEditProfileResponse();
        userEditProfileResponse.setRealName(user.getRealName());
        userEditProfileResponse.setCity(user.getCity());
        userEditProfileResponse.setBirthDate(user.getBirthDate());

        return userEditProfileResponse;
    }

    public static void mapUserEditProfileRequestToUserEntity (UserEntity user, UserEditProfileRequest userEditProfileRequest) {

        user.setRealName(userEditProfileRequest.getRealName());
        user.setCity(userEditProfileRequest.getCity());
        user.setBirthDate(userEditProfileRequest.getBirthDate());

    }

    public static UserChangeUsernameResponse mapUserEntityToUserChangeUsernameResponse (UserEntity user) {

        UserChangeUsernameResponse userChangeUsernameResponse = new UserChangeUsernameResponse();
        userChangeUsernameResponse.setOldUsername(user.getUsername());

        return userChangeUsernameResponse;
    }

    public static void mapUserChangeUsernameRequestToUserEntity (UserEntity user, UserChangeUsernameRequest userChangeUsernameRequest) {

        user.setUsername(userChangeUsernameRequest.getNewUsername());

    }

    public static UserChangeEmailResponse mapUserEntityToUserChangeEmailResponse (UserEntity user) {

        UserChangeEmailResponse userChangeEmailResponse = new UserChangeEmailResponse();
        userChangeEmailResponse.setOldEmail(user.getEmail());

        return userChangeEmailResponse;
    }

    public static void mapUserChangeEmailRequestToUserEntity (UserEntity user, UserChangeEmailRequest userChangeEmailRequest) {

        user.setEmail(userChangeEmailRequest.getNewEmail());

    }

    public static ChangeRoleUserResponse mapUserEntityToChangeRoleUserResponse (UserEntity user) {

        ChangeRoleUserResponse changeRoleUserResponse = new ChangeRoleUserResponse();
        changeRoleUserResponse.setId(user.getId());
        changeRoleUserResponse.setUsername(user.getUsername());
        changeRoleUserResponse.setRole(user.getRole().getRole().name());

        return changeRoleUserResponse;
    }

    public static BanUserResponse mapUserEntityToBanUserResponse (UserEntity user) {

        BanUserResponse banUserResponse = new BanUserResponse();
        banUserResponse.setId(user.getId());
        banUserResponse.setUsername(user.getUsername());
        banUserResponse.setEmail(user.getEmail());
        banUserResponse.setBanned(user.isBanned());
        banUserResponse.setReasonForBan(user.getReasonForBan());

        return banUserResponse;
    }

    public static ApproveUsersResponse mapUserEntityToApproveUsersResponse (UserEntity user) {

        ApproveUsersResponse approveUsersResponse = new ApproveUsersResponse();
        approveUsersResponse.setId(user.getId());
        approveUsersResponse.setUsername(user.getUsername());
        approveUsersResponse.setEmail(user.getEmail());

        return approveUsersResponse;
    }

    public static AdminPermissionsResponse mapUserEntityToAdminPermissionsResponse (UserEntity user) {

        AdminPermissionsResponse adminPermissionsResponse = new AdminPermissionsResponse();
        adminPermissionsResponse.setId(user.getId());
        adminPermissionsResponse.setUsername(user.getUsername());
        adminPermissionsResponse.setPermissions(user.getPermissions());

        return adminPermissionsResponse;
    }

    public static ModeratorPermissionsResponse mapUserEntityToModeratorPermissionsResponse (UserEntity user) {

        ModeratorPermissionsResponse moderatorPermissionsResponse = new ModeratorPermissionsResponse();
        moderatorPermissionsResponse.setId(user.getId());
        moderatorPermissionsResponse.setUsername(user.getUsername());
        moderatorPermissionsResponse.setPermissions(user.getPermissions());

        return moderatorPermissionsResponse;
    }

    public static FriendsResponse mapUserEntityToFriendsResponse (UserEntity user) {

        FriendsResponse friendsResponse = new FriendsResponse();
        friendsResponse.setUsername(user.getUsername());
        friendsResponse.setProfilePicturePath(user.getProfilePicturePath());

        return friendsResponse;
    }

    public static FollowersResponse mapUserEntityToFollowersResponse (UserEntity user) {

        FollowersResponse followersResponse = new FollowersResponse();
        followersResponse.setUsername(user.getUsername());
        followersResponse.setProfilePicturePath(user.getProfilePicturePath());

        return followersResponse;
    }

    public static BlockedUserResponse mapUserEntityToBlockedUserResponse (UserEntity user) {

        BlockedUserResponse blockedUserResponse = new BlockedUserResponse();
        blockedUserResponse.setUsername(user.getUsername());
        blockedUserResponse.setProfilePicturePath(user.getProfilePicturePath());

        return blockedUserResponse;
    }

    public static ContactUserResponse mapUserEntityToContactUserResponse (UserEntity user) {

        ContactUserResponse contactUserResponse = new ContactUserResponse();
        contactUserResponse.setEmail(user.getEmail());
        contactUserResponse.setRealName(user.getRealName());

        return contactUserResponse;
    }

    public static UserInformationForPictureResponse mapUserEntityToUserInformationForPictureResponse (UserEntity user) {

        UserInformationForPictureResponse userInformationForPictureResponse = new UserInformationForPictureResponse();
        userInformationForPictureResponse.setId(user.getId());
        userInformationForPictureResponse.setUsername(user.getUsername());
        userInformationForPictureResponse.setProfilePicturePath(user.getProfilePicturePath());

        return userInformationForPictureResponse;
    }

    public static UserEntity mapUserRegisterRequestToUserEntity (UserRegisterRequest userRegisterRequest) {

        UserEntity user = new UserEntity();
        user.setUsername(userRegisterRequest.getUsername());
        user.setEmail(userRegisterRequest.getEmail());
        user.setProfilePicturePath("https://res.cloudinary.com/dkyp0c0lz/image/upload/v1737304170/male-profile-picture_rltohq.avif");
        user.setBanned(false);
        user.setBirthDate(userRegisterRequest.getBirthDate());
        user.setApproved(false);
        user.setPoints(0);
        user.setRealName("Anonymous");
        user.setBirthDate(userRegisterRequest.getBirthDate());
        user.setGender(GenderEnum.fromString(userRegisterRequest.getGender()));

        return user;
    }

    public static UserProfileResponse mapUserEntityToUserProfileResponse (UserEntity user) {

        LocalDate birthDate = user.getBirthDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setUsername(user.getUsername());
        userProfileResponse.setProfilePicturePath(user.getProfilePicturePath());
        userProfileResponse.setCity(user.getCity());
        userProfileResponse.setCountry(user.getCountry().getName().getCountryName());
        userProfileResponse.setAge(age);
        userProfileResponse.setRank(user.getRank().getRank().name());
        userProfileResponse.setPoints(user.getPoints());
        userProfileResponse.setId(user.getId());
        userProfileResponse.setGender(user.getGender().getGenderType());
        userProfileResponse.setRealName(user.getRealName());

        return userProfileResponse;
    }
}

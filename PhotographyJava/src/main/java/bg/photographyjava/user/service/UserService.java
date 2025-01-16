package bg.photographyjava.user.service;

import bg.photographyjava.web.dto.*;
import bg.photographyjava.user.model.UserEntity;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    void seedUsers();

    Optional<UserEntity> getUserByEmail(String email);

    Optional<UserEntity> getUserByUsername(String username);

    void registerUser(UserRegisterDTO userRegisterDTO);

    String verify(UserLoginDTO userLoginDTO);

    UserProfileDTO getProfileDetails(String username);

    UserEditProfileDTO getProfileEditDetails(String username);

    void editUserDetails(String username, UserEditProfileDTO userEditProfileDTO);

    UserChangeUsernameDTO getUserUsernameDetails(String username);

    UserChangeEmailDTO getUserEmailDetails(String username);

    void editUserUsernameDetails(String username, UserChangeUsernameDTO userChangeUsernameDTO);

    void editUserEmailDetails(String username, UserChangeEmailDTO userChangeEmailDTO);

    void updatePassword(String username, String encodedNewPassword);

    List<ChangeRoleUserDTO> getAllUsers();

    void updateUserRole(UUID userId, String role, String username);
}

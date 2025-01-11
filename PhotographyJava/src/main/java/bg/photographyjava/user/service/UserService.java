package bg.photographyjava.user.service;

import bg.photographyjava.web.dto.UserLoginDTO;
import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.web.dto.UserProfileDTO;
import bg.photographyjava.web.dto.UserRegisterDTO;

import java.util.Optional;

public interface UserService {
    void seedUsers();

    Optional<UserEntity> getUserByEmail(String email);

    Optional<UserEntity> getUserByUsername(String username);

    void registerUser(UserRegisterDTO userRegisterDTO);

    String verify(UserLoginDTO userLoginDTO);

    UserProfileDTO getProfileDetails(String username);
}

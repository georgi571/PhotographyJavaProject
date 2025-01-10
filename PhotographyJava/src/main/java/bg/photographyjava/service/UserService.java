package bg.photographyjava.service;

import bg.photographyjava.model.dto.UserLoginDTO;
import bg.photographyjava.model.entity.UserEntity;
import bg.photographyjava.model.dto.UserRegisterDTO;

import java.util.Optional;

public interface UserService {
    void seedUsers();

    Optional<UserEntity> getUserByEmail(String email);

    Optional<UserEntity> getUserByUsername(String username);

    void registerUser(UserRegisterDTO userRegisterDTO);

    String verify(UserLoginDTO userLoginDTO);
}

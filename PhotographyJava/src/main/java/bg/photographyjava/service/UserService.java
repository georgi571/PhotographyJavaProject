package bg.photographyjava.service;

import bg.photographyjava.model.entity.User;
import bg.photographyjava.model.dto.UserRegisterDTO;

import java.util.Optional;

public interface UserService {
    void seedUsers();

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByUsername(String username);

    void registerUser(UserRegisterDTO userRegisterDTO);
}

package bg.photographyjava.web.controllers;

import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.web.dto.*;
import bg.photographyjava.user.property.enums.CountryEnum;
import bg.photographyjava.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //login user and return the twt token to FE

    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody UserLoginDTO userLoginDTO) {
        String jwtToken = this.userService.verify(userLoginDTO);

        if (jwtToken != null) {
            LoginResponseDTO loginResponse = new LoginResponseDTO(jwtToken);
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Send all country names to the FE

    @GetMapping("register")
    public RegisterInfoDTO getRegistrationInfo() {
        List<String> countries = CountryEnum.getCountryNames();

        return new RegisterInfoDTO(countries);
    }

    // Register user or send error to FE

    @PostMapping("register")
    public ResponseEntity<?> registerUser(
            @RequestBody @Valid UserRegisterDTO userRegisterDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errorResponse = new HashMap<>();

            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errorResponse.put(fieldName, errorMessage);
            });

            return ResponseEntity.badRequest().body(errorResponse);
        }

        userService.registerUser(userRegisterDTO);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // return full information about the user details

    @GetMapping("profile")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        String username = authentication.getName();
        UserProfileDTO userProfileDTO = this.userService.getProfileDetails(username);

        return ResponseEntity.ok(userProfileDTO);
    }
}

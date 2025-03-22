package bg.photographyjava.web.controllers;

import bg.photographyjava.user.property.enums.CountryEnum;
import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.dto.RegisterInfoResponse;
import bg.photographyjava.web.dto.UserLoginRequest;
import bg.photographyjava.web.dto.UserLoginResponse;
import bg.photographyjava.web.dto.UserRegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {

        String jwtToken = this.userService.verify(userLoginRequest);

        if (jwtToken != null) {
            UserLoginResponse loginResponse = new UserLoginResponse(jwtToken);
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new UserLoginResponse("Invalid credentials"));
        }
    }

    @GetMapping("/register")
    public ResponseEntity<RegisterInfoResponse> getRegistrationInfo() {

        return ResponseEntity.ok(new RegisterInfoResponse(CountryEnum.getCountryNames()));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(
            @RequestBody @Valid UserRegisterRequest userRegisterRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errorResponse = bindingResult.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            error -> Optional.ofNullable(error.getDefaultMessage()).orElse("Unknown validation error")
                    ));

            return ResponseEntity.badRequest().body(errorResponse);
        }

        this.userService.registerUser(userRegisterRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap("message", "User registered successfully"));
    }
}

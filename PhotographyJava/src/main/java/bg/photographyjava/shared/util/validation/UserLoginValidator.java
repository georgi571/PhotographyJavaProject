package bg.photographyjava.shared.util.validation;

import bg.photographyjava.shared.util.validation.annotation.ValidUserLogin;
import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.dto.UserLoginRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserLoginValidator implements ConstraintValidator<ValidUserLogin, Object> {

    private final UserService userService;

    public UserLoginValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(ValidUserLogin constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean isValid = false;
        if (value instanceof UserLoginRequest user) {

            if (user.getUsername() == null || user.getPassword() == null) {
                return false;
            }

            isValid = this.userService.isValidUser(user.getUsername(), user.getPassword());

            if (!isValid) {
            context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Invalid username or password")
                        .addConstraintViolation();
            }
        }
        return isValid;
    }
}

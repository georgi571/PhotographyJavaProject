package bg.photographyjava.shared.util.validation;

import bg.photographyjava.user.service.UserService;
import bg.photographyjava.shared.util.validation.annotation.UniqueEmail;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidator;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserService userService;

    public UniqueEmailValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return this.userService.getUserByEmail(value).isEmpty();
    }


}
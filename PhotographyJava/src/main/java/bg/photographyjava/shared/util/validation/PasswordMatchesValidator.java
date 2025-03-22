package bg.photographyjava.shared.util.validation;

import bg.photographyjava.web.dto.UserRegisterRequest;
import bg.photographyjava.shared.util.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof UserRegisterRequest user) {
            return user.getPassword().equals(user.getConfirmPassword());
        }
        return false;
    }
}

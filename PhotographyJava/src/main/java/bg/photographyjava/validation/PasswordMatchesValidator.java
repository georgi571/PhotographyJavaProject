package bg.photographyjava.validation;

import bg.photographyjava.model.dto.UserRegisterDTO;
import bg.photographyjava.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof UserRegisterDTO user) {
            return user.getPassword().equals(user.getConfirmPassword());
        }
        return false;
    }
}

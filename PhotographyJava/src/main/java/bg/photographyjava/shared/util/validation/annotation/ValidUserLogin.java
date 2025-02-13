package bg.photographyjava.shared.util.validation.annotation;

import bg.photographyjava.shared.util.validation.UserLoginValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UserLoginValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUserLogin {
    String message() default "Invalid username or password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

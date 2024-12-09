package bg.photographyjava.validation.annotation;

import bg.photographyjava.validation.UniqueUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    String message() default "{username.exist}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

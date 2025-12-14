package rmit.saintgiong.discoveryapi.internal.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that salaryMin is less than or equal to salaryMax when both are provided.
 */
@Documented
@Constraint(validatedBy = SalaryRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSalaryRange {
    String message() default "Minimum salary must be less than or equal to maximum salary";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

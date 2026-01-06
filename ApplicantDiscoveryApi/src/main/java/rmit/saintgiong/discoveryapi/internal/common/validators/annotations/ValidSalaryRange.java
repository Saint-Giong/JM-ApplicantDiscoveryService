package rmit.saintgiong.discoveryapi.internal.common.validators.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import rmit.saintgiong.discoveryapi.internal.common.validators.constraints.SalaryRangeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


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

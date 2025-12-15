package rmit.saintgiong.discoveryapi.internal.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Validator for salary range validation.
 * Ensures salaryMin <= salaryMax when both values are provided.
 * Works with any DTO that has getSalaryMin() and getSalaryMax() methods.
 */
@Slf4j
public class SalaryRangeValidator implements ConstraintValidator<ValidSalaryRange, Object> {

    @Override
    public void initialize(ValidSalaryRange constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null) {
            log.debug("Salary range validation skipped: DTO is null");
            return true;
        }

        try {
            // Use reflection to get salary values from any DTO
            Method getSalaryMin = dto.getClass().getMethod("getSalaryMin");
            Method getSalaryMax = dto.getClass().getMethod("getSalaryMax");

            Double salaryMin = (Double) getSalaryMin.invoke(dto);
            Double salaryMax = (Double) getSalaryMax.invoke(dto);

            // If either value is null, skip validation (partial updates allowed)
            if (salaryMin == null || salaryMax == null) {
                log.debug("Salary range validation skipped: salaryMin={}, salaryMax={}", salaryMin, salaryMax);
                return true;
            }

            // Validate that min <= max
            boolean isValid = salaryMin <= salaryMax;

            if (!isValid) {
                log.warn("Salary range validation failed: salaryMin ({}) > salaryMax ({})", salaryMin, salaryMax);
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Minimum salary (" + salaryMin + ") must be less than or equal to maximum salary (" + salaryMax + ")"
                ).addPropertyNode("salaryMin").addConstraintViolation();
            } else {
                log.debug("Salary range validation passed: salaryMin={}, salaryMax={}", salaryMin, salaryMax);
            }

            return isValid;

        } catch (NoSuchMethodException e) {
            // DTO doesn't have salary methods - skip validation
            log.debug("Salary range validation skipped: {} does not have salary methods", dto.getClass().getSimpleName());
            return true;
        } catch (Exception e) {
            log.error("Salary range validation error for {}: {}", dto.getClass().getSimpleName(), e.getMessage(), e);
            return false;
        }
    }
}

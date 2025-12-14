package rmit.saintgiong.discoveryapi.internal.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import rmit.saintgiong.discoveryapi.internal.dto.CreateSearchProfileRequestDto;

/**
 * Validator for salary range validation.
 * Ensures salaryMin <= salaryMax when both values are provided.
 */
public class SalaryRangeValidator implements ConstraintValidator<ValidSalaryRange, CreateSearchProfileRequestDto> {

    @Override
    public void initialize(ValidSalaryRange constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(CreateSearchProfileRequestDto dto, ConstraintValidatorContext context) {
        // If either value is null, skip validation
        if (dto.getSalaryMin() == null || dto.getSalaryMax() == null) {
            return true;
        }

        // Validate that min <= max
        boolean isValid = dto.getSalaryMin() <= dto.getSalaryMax();

        if (!isValid) {
            // Customize error message to be associated with salaryMin field
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Minimum salary (" + dto.getSalaryMin() + ") must be less than or equal to maximum salary (" + dto.getSalaryMax() + ")"
            ).addPropertyNode("salaryMin").addConstraintViolation();
        }

        return isValid;
    }
}

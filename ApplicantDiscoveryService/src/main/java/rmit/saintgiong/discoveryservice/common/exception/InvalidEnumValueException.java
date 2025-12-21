package rmit.saintgiong.discoveryservice.common.exception;

/**
 * Exception thrown when an invalid enum value is provided in a request.
 * Used to handle invalid degree types, employment types, and other enum-based fields.
 */
public class InvalidEnumValueException extends RuntimeException {

    private final String fieldName;
    private final String invalidValue;
    private final String[] validValues;

    public InvalidEnumValueException(String fieldName, String invalidValue, String[] validValues) {
        super(buildMessage(fieldName, invalidValue, validValues));
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.validValues = validValues;
    }

    private static String buildMessage(String fieldName, String invalidValue, String[] validValues) {
        return String.format("Invalid %s '%s'. Valid values are: %s",
                fieldName, invalidValue, String.join(", ", validValues));
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public String[] getValidValues() {
        return validValues;
    }
}

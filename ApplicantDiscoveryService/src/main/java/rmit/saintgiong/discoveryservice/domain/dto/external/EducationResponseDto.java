package rmit.saintgiong.discoveryservice.domain.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EducationResponseDto(
    String institutionName,
    String degree,
    Double gpa,
    String description,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean isCurrent
) {}

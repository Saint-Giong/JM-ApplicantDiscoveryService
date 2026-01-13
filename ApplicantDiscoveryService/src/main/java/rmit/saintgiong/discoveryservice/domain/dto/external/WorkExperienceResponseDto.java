package rmit.saintgiong.discoveryservice.domain.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkExperienceResponseDto(
    String companyName,
    String position,
    String description,
    String country,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean isCurrent
) {}

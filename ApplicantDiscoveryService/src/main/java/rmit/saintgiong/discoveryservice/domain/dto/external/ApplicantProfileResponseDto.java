package rmit.saintgiong.discoveryservice.domain.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApplicantProfileResponseDto(
    UUID applicantId,
    String firstName,
    String lastName,
    String phone,
    String address,
    String city,
    String biography,
    String country,
    List<EducationResponseDto> educationList,
    List<WorkExperienceResponseDto> workExperienceList,
    List<Long> skillIds,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String aboutMe) {
}

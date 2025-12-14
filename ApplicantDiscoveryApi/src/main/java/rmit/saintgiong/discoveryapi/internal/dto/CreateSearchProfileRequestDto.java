package rmit.saintgiong.discoveryapi.internal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating a search profile")
public class CreateSearchProfileRequestDto {

    @PositiveOrZero(message = "Minimum salary must be zero or positive")
    @Schema(description = "Minimum salary range", example = "50000.0")
    private Double salaryMin;

    @PositiveOrZero(message = "Maximum salary must be zero or positive")
    @Schema(description = "Maximum salary range", example = "100000.0")
    private Double salaryMax;

    @Schema(description = "Highest education degree required", example = "BACHELOR")
    private String highestDegree;

    @Schema(description = "Employment type as Set (Full-time, Part-time, Fresher, Internship, Contract)", example = "[Fresher, Full-time, Contract]")
    private Set<String> employmentTypes;

    @Schema(description = "Country for the search", example = "Vietnam")
    private String country;

    @NotNull(message = "Company ID is required")
    @Schema(description = "Company ID that owns this search profile", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID companyId; //TODO: for regular company user, company id should be from access token instead of request body

    @Schema(description = "List of skill tag technical background filtering")
    private Set<Integer> skillTagIds;
}

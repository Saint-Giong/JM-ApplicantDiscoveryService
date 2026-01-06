package rmit.saintgiong.discoveryapi.internal.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response payload for search profile data")
public class SearchProfileResponseDto {

    @Schema(description = "Unique identifier of the search profile")
    private UUID profileId;

    @Schema(description = "Minimum salary range", example = "50000.0")
    private Double salaryMin;

    @Schema(description = "Maximum salary range", example = "100000.0")
    private Double salaryMax;

    @Schema(description = "Highest education degree required", example = "BACHELOR")
    private String highestDegree;

    @Schema(description = "Employment types selected", example = "[0, 1, 2]")
    private Set<String> employmentTypes;

    @Schema(description = "Country for the search", example = "Vietnam")
    private String country;

    @Schema(description = "Company ID that owns this search profile")
    private UUID companyId;

    @Schema(description = "List of skill tag IDs for technical background filtering")
    private Set<Integer> skillTagIds;
}

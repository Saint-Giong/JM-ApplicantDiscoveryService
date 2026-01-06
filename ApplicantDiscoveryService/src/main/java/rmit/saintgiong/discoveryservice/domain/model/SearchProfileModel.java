package rmit.saintgiong.discoveryservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rmit.saintgiong.discoveryapi.internal.common.types.type.DegreeType;

import java.util.BitSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchProfileModel {

    private UUID profileId;

    private Double salaryMin;

    private Double salaryMax;

    private DegreeType highestDegree;

    private BitSet employmentType;

    private String country;

    private UUID companyId;

    private Set<SearchProfile_SkillTagModel> skillTags;
}

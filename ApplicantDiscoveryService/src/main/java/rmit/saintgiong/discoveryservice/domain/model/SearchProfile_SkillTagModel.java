package rmit.saintgiong.discoveryservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchProfile_SkillTagModel {
    private UUID profileId;
    private UUID tagId;
}

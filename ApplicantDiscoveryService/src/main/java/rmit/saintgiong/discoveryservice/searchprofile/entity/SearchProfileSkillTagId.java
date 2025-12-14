package rmit.saintgiong.discoveryservice.searchprofile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchProfileSkillTagId implements Serializable {
    
    @Column(name = "profile_id")
    private UUID profileId;
    
    @Column(name = "tag_id")
    private UUID tagId;
}

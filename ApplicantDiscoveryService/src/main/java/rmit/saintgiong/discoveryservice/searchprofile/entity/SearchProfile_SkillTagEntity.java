package rmit.saintgiong.discoveryservice.searchprofile.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "searchprofile_skilltag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString(exclude = "searchProfile")
@Builder
public class SearchProfile_SkillTagEntity {

    @EmbeddedId
    private SearchProfileSkillTagId id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("profileId")
    @JoinColumn(name = "profile_id", nullable = false)
    private SearchProfileEntity searchProfile;

    @Column(name = "tag_id", insertable = false, updatable = false)
    private UUID tagId;

}

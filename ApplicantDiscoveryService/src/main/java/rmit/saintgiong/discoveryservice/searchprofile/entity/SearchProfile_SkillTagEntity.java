package rmit.saintgiong.discoveryservice.searchprofile.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Entity(name = "search_profile_skill_tag")
@Getter
@Setter
public class SearchProfile_SkillTagEntity {

    @EmbeddedId
    private SearchProfileSkillTagId skillTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("profileId") // Links to 'profileId' field in SearchProfileSkillTagId
    @JoinColumn(name = "profile_id")
    private SearchProfileEntity searchProfile;

    // Constructors
    public SearchProfile_SkillTagEntity() {}
    public SearchProfile_SkillTagEntity(SearchProfileEntity profile, Integer tagId) {
        this.searchProfile = profile;
        this.skillTagId = new SearchProfileSkillTagId(profile.getProfileId(), tagId);
    }

}

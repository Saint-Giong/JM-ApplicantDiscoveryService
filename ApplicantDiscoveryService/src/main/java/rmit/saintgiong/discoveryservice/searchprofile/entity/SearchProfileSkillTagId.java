package rmit.saintgiong.discoveryservice.searchprofile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class SearchProfileSkillTagId implements Serializable {

    @Column(name = "profile_id")
    private UUID profileId;

    @Column(name = "tag_id")
    private Integer tagId;

    public SearchProfileSkillTagId() {}

    public SearchProfileSkillTagId(UUID profileId, Integer tagId) {
        this.profileId = profileId;
        this.tagId = tagId;
    }

    // Getters and Setters
    public UUID getProfileId() { return profileId; }
    public void setProfileId(UUID profileId) { this.profileId = profileId; }
    public Integer getTagId() { return tagId; }
    public void setTagId(Integer tagId) { this.tagId = tagId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchProfileSkillTagId that = (SearchProfileSkillTagId) o;
        return Objects.equals(profileId, that.profileId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileId, tagId);
    }
}
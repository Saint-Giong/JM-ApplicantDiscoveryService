package rmit.saintgiong.discoveryservice.searchprofile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import rmit.saintgiong.discoveryservice.common.degree.type.DegreeType;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EntityListeners(AuditingEntityListener.class)
@Entity(name = "search_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "skillTags")
@Builder
public class SearchProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID profileId;

    @Column(name = "salary_min")
    private Double salaryMin;

    @Column(name = "salary_max")
    private Double salaryMax;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "highest_degree")
    private DegreeType highestDegree;

    @Column(name = "employment_type")
    private BitSet employmentType;

    @Column(name = "country")
    private String country;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @OneToMany(mappedBy = "searchProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SearchProfile_SkillTagEntity> skillTags;

    public void addSkillTag(Integer tagId) {
        SearchProfile_SkillTagEntity skillTagEntity = new SearchProfile_SkillTagEntity(this, tagId);

        if (this.skillTags == null) {
            this.skillTags = new HashSet<>();
        }

        this.skillTags.add(skillTagEntity);
        skillTagEntity.setSearchProfile(this);
    }
}

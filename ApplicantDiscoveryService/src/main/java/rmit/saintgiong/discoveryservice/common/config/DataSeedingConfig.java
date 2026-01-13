package rmit.saintgiong.discoveryservice.common.config;

import java.util.BitSet;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rmit.saintgiong.discoveryapi.internal.common.types.type.EmploymentTypeEnum;
import rmit.saintgiong.discoveryservice.domain.entity.SearchProfileEntity;
import rmit.saintgiong.discoveryservice.domain.services.SearchProfileRepository;


@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeedingConfig implements CommandLineRunner {

    private final SearchProfileRepository searchProfileRepository;

    // Company UUIDs - must match Auth service (only Premiums have search profiles)
    private static final UUID NETCOMPANY_COMPANY_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID SHOPEE_COMPANY_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    // Skill Tag IDs - must match SkillTag service
    private static final int TAG_PYTHON = 2;        // PYTHON
    private static final int TAG_REACT = 5;         // REACT
    private static final int TAG_SPRING_BOOT = 8;   // SPRING BOOT
    private static final int TAG_SNOWFLAKE = 10;    // SNOWFLAKE
    private static final int TAG_DOCKER = 12;       // DOCKER
    private static final int TAG_AWS = 14;          // AWS

    @Override
    public void run(String... args) {
        if (searchProfileRepository.count() > 0) {
            return;
        }

        log.info("Seeding Search Profiles for Premium companies...");

        // ============================================================
        // PREMIUM 1: Netcompany - Software Engineering Domain
        // Search Profile: React, Spring Boot, Docker talents
        // - Full-time + Intern Software Engineer
        // - Vietnam, salary > 800 USD
        // ============================================================
        createNetcompanySearchProfile();

        // ============================================================
        // PREMIUM 2: Shopee - Data Engineering Domain
        // Search Profile: Python, AWS, Snowflake talents
        // - Contractual Data Engineer
        // - Singapore, salary > 1200 USD
        // ============================================================
        createShopeeSearchProfile();

        log.info("Seeded search profiles for 2 Premium companies.");
    }

    /**
     * Netcompany Search Profile:
     * - Skills: React, Spring Boot, Docker
     * - Employment Types: FULL_TIME (0), INTERNSHIP (3)
     * - Country: Vietnam
     * - Salary Min: > 800 USD
     */
    private void createNetcompanySearchProfile() {
        // Create BitSet for employment types: FULL_TIME (0) and INTERNSHIP (3)
        BitSet employmentTypes = new BitSet();
        employmentTypes.set(EmploymentTypeEnum.FULL_TIME.getBitIndex());    // bit 0
        employmentTypes.set(EmploymentTypeEnum.INTERNSHIP.getBitIndex());   // bit 3

        SearchProfileEntity profile = SearchProfileEntity.builder()
                .companyId(NETCOMPANY_COMPANY_ID)
                .country("Vietnam")
                .salaryMin(800.0)  // > 800 USD
                .salaryMax(null)   // No upper limit
                .employmentType(employmentTypes)
                .highestDegree(null) // Any degree accepted
                .build();

        // Add skill tags: React (5), Spring Boot (8), Docker (12)
        profile.addSkillTag(TAG_REACT);
        profile.addSkillTag(TAG_SPRING_BOOT);
        profile.addSkillTag(TAG_DOCKER);

        searchProfileRepository.save(profile);
        log.info("Created Netcompany search profile: React, Spring Boot, Docker | Full-time/Intern | Vietnam | >800 USD");
    }

    /**
     * Shopee Search Profile:
     * - Skills: Python, AWS, Snowflake
     * - Employment Types: CONTRACT (4)
     * - Country: Singapore
     * - Salary Min: > 1200 USD
     */
    private void createShopeeSearchProfile() {
        // Create BitSet for employment types: CONTRACT (4)
        BitSet employmentTypes = new BitSet();
        employmentTypes.set(EmploymentTypeEnum.CONTRACT.getBitIndex());     // bit 4

        SearchProfileEntity profile = SearchProfileEntity.builder()
                .companyId(SHOPEE_COMPANY_ID)
                .country("Singapore")
                .salaryMin(1200.0)  // > 1200 USD
                .salaryMax(null)    // No upper limit
                .employmentType(employmentTypes)
                .highestDegree(null) // Any degree accepted
                .build();

        // Add skill tags: Python (2), AWS (14), Snowflake (10)
        profile.addSkillTag(TAG_PYTHON);
        profile.addSkillTag(TAG_AWS);
        profile.addSkillTag(TAG_SNOWFLAKE);

        searchProfileRepository.save(profile);
        log.info("Created Shopee search profile: Python, AWS, Snowflake | Contract | Singapore | >1200 USD");
    }
}

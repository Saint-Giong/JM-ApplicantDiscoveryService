package rmit.saintgiong.discoveryservice.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rmit.saintgiong.discoveryservice.domain.entity.SearchProfileEntity;
import rmit.saintgiong.discoveryservice.domain.services.SearchProfileRepository;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeedingConfig implements CommandLineRunner {

    private final SearchProfileRepository searchProfileRepository;

    @Override
    public void run(String... args) {
        if (searchProfileRepository.count() > 0) {
            return;
        }

        createAndSaveProfile(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Australia", 1, 3);
        createAndSaveProfile(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Germany", 4);

        createAndSaveProfile(UUID.fromString("33333333-3333-3333-3333-333333333333"), "Vietnam", 2, 6);
        createAndSaveProfile(UUID.fromString("33333333-3333-3333-3333-333333333333"), "France", 5);

        createAndSaveProfile(UUID.fromString("44444444-4444-4444-4444-444444444444"), "United States", 1);
        createAndSaveProfile(UUID.fromString("44444444-4444-4444-4444-444444444444"), "United Kingdom", 2);

        createAndSaveProfile(UUID.fromString("55555555-5555-5555-5555-555555555555"), "Japan", 3);
        createAndSaveProfile(UUID.fromString("55555555-5555-5555-5555-555555555555"), "South Korea", 4);

        log.info("Seeded search profiles for companies.");
    }

    private void createAndSaveProfile(UUID companyId, String country, Integer... skillTags) {
        SearchProfileEntity p = new SearchProfileEntity();
        p.setCompanyId(companyId);
        p.setCountry(country);
        for (Integer tag : skillTags) {
            p.addSkillTag(tag);
        }
        searchProfileRepository.save(p);
    }
}

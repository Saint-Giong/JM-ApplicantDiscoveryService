package rmit.saintgiong.discoveryservice.domain.services.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Internal Document Dependencies
import rmit.saintgiong.discoveryapi.internal.common.types.type.KafkaTopic;
import rmit.saintgiong.discoveryapi.internal.service.elasticsearch.IndexingInterface;
import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;
import rmit.saintgiong.discoveryapi.internal.document.Education;
import rmit.saintgiong.discoveryapi.internal.document.WorkExperience;
// External Services and Repositories
import rmit.saintgiong.discoveryapi.external.services.ExternalDiscoveryRequestInterface;
import rmit.saintgiong.discoveryservice.domain.services.SearchProfileRepository;
import rmit.saintgiong.discoveryservice.domain.entity.SearchProfileEntity;


// Avro Imports
import rmit.saintgiong.jobapplicant.userprofile.avro.JaApplicantCreatedEvent;
import rmit.saintgiong.jobapplicant.userprofile.avro.JaApplicantUpdatedEvent;
import rmit.saintgiong.jobapplicant.userprofile.avro.EducationEvent;
import rmit.saintgiong.jobapplicant.userprofile.avro.WorkExperienceEvent;
import rmit.saintgiong.discoveryapi.external.dto.avro.ApplicantMatchNotificationRecord; // Generated Avro

@Service
public class CloudKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(CloudKafkaConsumer.class);
    private final IndexingInterface indexingInterface;
    private final ExternalDiscoveryRequestInterface externalDiscoveryRequestService;
    private final SearchProfileRepository searchProfileRepository;

    public CloudKafkaConsumer(
            IndexingInterface indexingInterface,
            ExternalDiscoveryRequestInterface externalDiscoveryRequestService,
            SearchProfileRepository searchProfileRepository
    ) {
        this.indexingInterface = indexingInterface;
        this.externalDiscoveryRequestService = externalDiscoveryRequestService;
        this.searchProfileRepository = searchProfileRepository;
    }

    @KafkaListener(
            topics = KafkaTopic.JA_APPLICANT_CREATED_TOPIC,
            containerFactory = "cloudKafkaListenerContainerFactory"
    )
    public void consumeApplicantAdd(ConsumerRecord<String, JaApplicantCreatedEvent> record) {
        JaApplicantCreatedEvent event = record.value();
        String userId = String.valueOf(event.getUserId());

        try {
            log.info("Consuming ADD event for userId: {}", userId);

            ApplicantDocument document = mapCreatedToDocument(event);
            indexingInterface.indexApplicant(document);

            log.info("Successfully indexed ADDED applicant: {}", userId);

            // Check for matches (isUpdate = false)
            checkMatches(document, false);

        } catch (Exception e) {
            log.error("Error consuming applicant ADD event for userId: {}", userId, e);
        }
    }

    @KafkaListener(
            topics = KafkaTopic.JA_APPLICANT_UPDATED_TOPIC,
            containerFactory = "cloudKafkaListenerContainerFactory"
    )
    public void consumeApplicantUpdate(ConsumerRecord<String, JaApplicantUpdatedEvent> record) {
        JaApplicantUpdatedEvent event = record.value();
        String userId = String.valueOf(event.getUserId());

        try {
            log.info("Consuming UPDATE event for userId: {}", userId);

            ApplicantDocument document = mapToDocument(event);
            indexingInterface.indexApplicant(document);

            log.info("Successfully indexed UPDATED applicant: {}", userId);

            // Check for matches (isUpdate = true)
            checkMatches(document, true);

        } catch (Exception e) {
            log.error("Error consuming applicant UPDATE event for userId: {}", userId, e);
        }
    }

    @KafkaListener(
            topics = KafkaTopic.JA_APPLICANT_DELETED_TOPIC,
            containerFactory = "cloudKafkaListenerContainerFactory"
    )
    public void consumeApplicantDelete(ConsumerRecord<String, JaApplicantUpdatedEvent> record) {
        JaApplicantUpdatedEvent event = record.value();
        String userId = String.valueOf(event.getUserId());

        try {
            log.info("Consuming DELETE event for userId: {}", userId);

            indexingInterface.deleteApplicant(userId);

            log.info("Successfully deleted applicant index: {}", userId);

        } catch (Exception e) {
            log.error("Error consuming applicant DELETE event for userId: {}", userId, e);
        }
    }
    

    // --- Matching Logic ---

    private void checkMatches(ApplicantDocument applicant, boolean isUpdate) {
        try {
            // 1. Get all premium company IDs
            List<Object> companyIdObjects = externalDiscoveryRequestService.sendGetAllPremiumCompaniesRequest();
            if (companyIdObjects == null || companyIdObjects.isEmpty()) {
                log.info("No premium companies found or error fetching them.");
                return;
            }

            List<UUID> premiumCompanyIds = companyIdObjects.stream()
                    .map(obj -> {
                        try {
                            return UUID.fromString(obj.toString());
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (premiumCompanyIds.isEmpty()) {
                return;
            }

            // 2. Get search profiles for these companies
            List<SearchProfileEntity> searchProfiles = searchProfileRepository.findByCompanyIdIn(premiumCompanyIds);
            
            // MOCK DATA FOR TESTING
            if (premiumCompanyIds.contains(UUID.fromString("22222222-2222-2222-2222-222222222222"))) {
                log.info("Injecting mock search profiles for test company 22222222-2222-2222-2222-222222222222");
                
                // Mock 1: Matches Alice (Australia)
                SearchProfileEntity mock1 = new SearchProfileEntity();
                mock1.setProfileId(UUID.randomUUID());
                mock1.setCompanyId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
                mock1.setCountry("Australia");
                
                // Mock 2: Matches Huân (Vietnam, Skills 2, 6)
                SearchProfileEntity mock2 = new SearchProfileEntity();
                mock2.setProfileId(UUID.randomUUID());
                mock2.setCompanyId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
                mock2.setCountry("Vietnam");
                mock2.addSkillTag(2);
                mock2.addSkillTag(6); // Applicant has 2, 4, 6. Required 2, 6 is a subset of applicant's skills -> Match.

                if (searchProfiles == null) {
                    searchProfiles = new java.util.ArrayList<>();
                }
                searchProfiles.add(mock1);
                searchProfiles.add(mock2);
            }
            
            log.info("Found {} search profiles from premium companies to check against applicant {}", searchProfiles.size(), applicant.applicantId());

            // 3. Compare applicant against profiles
            for (SearchProfileEntity profile : searchProfiles) {
                if (isMatch(applicant, profile)) {
                    log.info("MATCH FOUND: Applicant {} matches Search Profile {} (Company {})", 
                        applicant.applicantId(), profile.getProfileId(), profile.getCompanyId());
                    
                    // Send notification
                    ApplicantMatchNotificationRecord notification = ApplicantMatchNotificationRecord.newBuilder()
                            .setApplicantId(applicant.applicantId())
                            .setCompanyId(profile.getCompanyId())
                            .setSearchProfileId(profile.getProfileId())
                            .build();

                    externalDiscoveryRequestService.sendMatchNotification(notification, isUpdate);
                }
            }

        } catch (Exception e) {
            log.error("Error checking matches for applicant {}", applicant.applicantId(), e);
        }
    }

    private boolean isMatch(ApplicantDocument applicant, SearchProfileEntity profile) {
        // 1. Country Check
        if (profile.getCountry() != null && !profile.getCountry().isEmpty()) {
            if (applicant.country() == null || !applicant.country().equalsIgnoreCase(profile.getCountry())) {
                return false;
            }
        }

        // 2. Degree Check
        if (profile.getHighestDegree() != null) {
            boolean degreeMatches = false;
            if (applicant.educations() != null) {
                for (Education edu : applicant.educations()) {
                    if (edu.degree() != null && edu.degree().toUpperCase().contains(profile.getHighestDegree().name())) {
                        degreeMatches = true;
                        break;
                    }
                }
            }
            if (!degreeMatches) return false;
        }

        // 3. Skills Check (Applicant must have ALL required skills)
        if (profile.getSkillTags() != null && !profile.getSkillTags().isEmpty()) {
            if (applicant.skillIds() == null || applicant.skillIds().isEmpty()) {
                return false;
            }
            // Convert applicant's Long IDs to Integer for comparison (handling potential type mismatch)
            Set<Integer> applicantSkillIds = applicant.skillIds().stream()
                    .map(Long::intValue)
                    .collect(Collectors.toSet());

            Set<Integer> requiredSkillIds = profile.getSkillTags().stream()
                    .map(tagEntity -> tagEntity.getSkillTagId().getTagId())
                    .collect(Collectors.toSet());

            if (!applicantSkillIds.containsAll(requiredSkillIds)) {
                return false;
            }
        }

        return true;
    }


    private ApplicantDocument mapToDocument(JaApplicantUpdatedEvent event) {
        List<Education> educations = (event.getEducations() != null)
                ? event.getEducations().stream().map(this::mapEducation).collect(Collectors.toList())
                : Collections.emptyList();

        List<WorkExperience> experiences = (event.getWorkExperiences() != null)
                ? event.getWorkExperiences().stream().map(this::mapWorkExperience).collect(Collectors.toList())
                : Collections.emptyList();

        List<Long> skillIds = (event.getSkillIds() != null) ? event.getSkillIds() : Collections.emptyList();
        List<String> skillNames = (event.getSkillNames() != null)
                ? event.getSkillNames().stream().map(Object::toString).collect(Collectors.toList())
                : Collections.emptyList();

        return new ApplicantDocument(
                UUID.fromString(event.getUserId().toString()),
                toStringSafe(event.getFirstName()),
                toStringSafe(event.getLastName()),
                toStringSafe(event.getPhone()),
                toStringSafe(event.getAddress()),
                toStringSafe(event.getCity()),
                toStringSafe(event.getBiography()),
                toStringSafe(event.getAboutMe()),
                toStringSafe(event.getAvatarUrl()),
                toStringSafe(event.getCountry()),
                educations,
                experiences,
                skillIds,
                skillNames,
                LocalDateTime.now(), // createdAt (refreshed on sync)
                LocalDateTime.now()  // updatedAt
        );
    }

    private Education mapEducation(EducationEvent e) {
        return new Education(
                toStringSafe(e.getInstitutionName()),
                toStringSafe(e.getDegree()),
                e.getGpa(),
                toStringSafe(e.getDescription()),
                parseDate(toStringSafe(e.getStartDate())),
                parseDate(toStringSafe(e.getEndDate())),
                e.getIsCurrent()
        );
    }

    private WorkExperience mapWorkExperience(WorkExperienceEvent w) {
        return new WorkExperience(
                toStringSafe(w.getCompanyName()),
                toStringSafe(w.getPosition()),
                toStringSafe(w.getDescription()),
                toStringSafe(w.getCountry()),
                parseDate(toStringSafe(w.getStartDate())),
                parseDate(toStringSafe(w.getEndDate())),
                w.getIsCurrent()
        );
    }

    private String toStringSafe(CharSequence cs) {
        return cs != null ? cs.toString() : null;
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDateTime.parse(dateStr);
        } catch (Exception e) {
            // Fallback or log if date format doesn't match
            return null;
        }
    }

    private ApplicantDocument mapCreatedToDocument(JaApplicantCreatedEvent event) {
        // The CreatedEvent schema only has basic info, so we initialize lists as empty
        // and missing strings as null.
        return new ApplicantDocument(
                UUID.fromString(event.getUserId().toString()),
                toStringSafe(event.getFirstName()),
                toStringSafe(event.getLastName()),
                toStringSafe(event.getPhone()),
                toStringSafe(event.getAddress()),
                toStringSafe(event.getCity()),
                null, // Biography not in CreatedEvent
                null, // AboutMe not in CreatedEvent
                null, // AvatarUrl not in CreatedEvent
                toStringSafe(event.getCountry()),
                Collections.emptyList(), // Educations
                Collections.emptyList(), // Work Experiences
                Collections.emptyList(), // SkillIds
                Collections.emptyList(), // SkillNames
                LocalDateTime.now(),     // CreatedAt
                LocalDateTime.now()      // UpdatedAt
        );
    }
}
package rmit.saintgiong.discoveryservice.domain.services.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Internal Document Dependencies
import rmit.saintgiong.discoveryapi.internal.common.types.type.KafkaTopic;
import rmit.saintgiong.discoveryapi.internal.service.elasticsearch.IndexingInterface;
import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;
import rmit.saintgiong.discoveryapi.internal.document.Education;
import rmit.saintgiong.discoveryapi.internal.document.WorkExperience;

// Avro Imports
import rmit.saintgiong.jobapplicant.userprofile.avro.JaApplicantCreatedEvent;
import rmit.saintgiong.jobapplicant.userprofile.avro.JaApplicantUpdatedEvent;
import rmit.saintgiong.jobapplicant.userprofile.avro.EducationEvent;
import rmit.saintgiong.jobapplicant.userprofile.avro.WorkExperienceEvent;

@Service
public class CloudKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(CloudKafkaConsumer.class);
    private final IndexingInterface indexingInterface;

    public CloudKafkaConsumer(IndexingInterface indexingInterface) {
        this.indexingInterface = indexingInterface;
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
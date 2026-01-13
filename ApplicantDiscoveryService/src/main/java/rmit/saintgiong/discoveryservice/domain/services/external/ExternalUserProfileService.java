package rmit.saintgiong.discoveryservice.domain.services.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;
import rmit.saintgiong.discoveryapi.internal.document.Education;
import rmit.saintgiong.discoveryapi.internal.document.WorkExperience;
import rmit.saintgiong.discoveryapi.internal.service.elasticsearch.IndexingInterface;
import rmit.saintgiong.discoveryservice.domain.dto.external.ApplicantProfileResponseDto;
import rmit.saintgiong.discoveryservice.domain.dto.external.EducationResponseDto;
import rmit.saintgiong.discoveryservice.domain.dto.external.WorkExperienceResponseDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalUserProfileService {

    private final IndexingInterface indexingInterface;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<ApplicantDocument> syncApplicants() {
        String url = "https://sgja-api.vohoangphuc.com/user-profile/applicants";
        log.info("Syncing applicants from {}", url);

        List<ApplicantDocument> indexedDocs = new java.util.ArrayList<>();

        try {
            ResponseEntity<List<ApplicantProfileResponseDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ApplicantProfileResponseDto>>() {}
            );

            List<ApplicantProfileResponseDto> dtos = response.getBody();
            if (dtos != null && !dtos.isEmpty()) {
                log.info("Fetched {} applicants. Indexing...", dtos.size());
                int successCount = 0;
                for (ApplicantProfileResponseDto dto : dtos) {
                    try {
                        ApplicantDocument doc = mapToDocument(dto);
                        indexingInterface.indexApplicant(doc);
                        indexedDocs.add(doc);
                        successCount++;
                    } catch (Exception e) {
                        log.error("Failed to map/index applicant {}", dto.applicantId(), e);
                    }
                }
                log.info("Sync completed. Successfully indexed {}/{} applicants.", successCount, dtos.size());
            } else {
                log.info("No applicants found to sync.");
            }

        } catch (Exception e) {
            log.error("Failed to sync applicants", e);
            throw new RuntimeException("Failed to sync applicants", e);
        }
        return indexedDocs;
    }

    private ApplicantDocument mapToDocument(ApplicantProfileResponseDto dto) {
        List<Education> educations = (dto.educationList() != null)
                ? dto.educationList().stream().map(this::mapEducation).collect(Collectors.toList())
                : Collections.emptyList();

        List<WorkExperience> experiences = (dto.workExperienceList() != null)
                ? dto.workExperienceList().stream().map(this::mapWorkExperience).collect(Collectors.toList())
                : Collections.emptyList();

        String countryName = dto.country();
        
        List<String> skillNames = Collections.emptyList(); // Not available in DTO

        return new ApplicantDocument(
                dto.applicantId(),
                trimToEmpty(dto.firstName()),
                trimToEmpty(dto.lastName()),
                trimToEmpty(dto.phone()),
                trimToEmpty(dto.address()),
                trimToEmpty(dto.city()),
                trimToEmpty(dto.biography()),
                trimToEmpty(dto.aboutMe()),
                null, // avatarUrl not in DTO
                trimToEmpty(countryName),
                educations,
                experiences,
                dto.skillIds(),
                skillNames,
                dto.createdAt(),
                dto.updatedAt()
        );
    }
    
    private String trimToEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private Education mapEducation(EducationResponseDto eduDto) {
        return new Education(
                eduDto.institutionName(),
                eduDto.degree(),
                eduDto.gpa(),
                eduDto.description(),
                eduDto.startDate(),
                eduDto.endDate(),
                eduDto.isCurrent()
        );
    }

    private WorkExperience mapWorkExperience(WorkExperienceResponseDto workDto) {
        return new WorkExperience(
                workDto.companyName(),
                workDto.position(),
                workDto.description(),
                workDto.country(),
                workDto.startDate(),
                workDto.endDate(),
                workDto.isCurrent()
        );
    }
}

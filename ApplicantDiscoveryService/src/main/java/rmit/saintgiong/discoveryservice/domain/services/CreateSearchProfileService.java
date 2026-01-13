package rmit.saintgiong.discoveryservice.domain.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rmit.saintgiong.discoveryapi.external.services.ExternalDiscoveryRequestInterface;
import rmit.saintgiong.discoveryapi.internal.service.InternalCreateSearchProfileInterface;
import rmit.saintgiong.discoveryapi.internal.common.dto.CreateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.common.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryservice.domain.entity.SearchProfileEntity;
import rmit.saintgiong.discoveryservice.domain.mapper.SearchProfileMapper;

@Service
@AllArgsConstructor
@Slf4j
public class CreateSearchProfileService implements InternalCreateSearchProfileInterface {

    private final SearchProfileRepository searchProfileRepository;
    private final SearchProfileMapper searchProfileMapper;
    private final ExternalDiscoveryRequestInterface externalDiscoveryRequestInterface;

    /**
     * Creates a new search profile for applicant discovery.
     * 
     * @param request the search profile creation request containing salary range,
     *                degree requirements, employment types, country, company ID,
     *                and skill tag IDs
     * @return a {@link SearchProfileResponseDto} containing the created profile's
     *         details including the generated profile ID
     * @throws IllegalArgumentException if the request is null
     */
    @Override
    @Transactional
    public SearchProfileResponseDto createSearchProfile(CreateSearchProfileRequestDto request) {
        // Validate request is not null
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        Boolean companyPremiumStatus = externalDiscoveryRequestInterface.sendGetCompanyPremiumStatusRequest(request.getCompanyId());

        if(companyPremiumStatus == null || !companyPremiumStatus) {
            throw new IllegalArgumentException("Company is not premium or does not exist: " + request.getCompanyId());
        }

        log.info("Creating search profile for company: {}", request.getCompanyId());



        // Convert the request DTO to entity, mapping basic fields
        SearchProfileEntity entity = searchProfileMapper.requestDtoToEntity(request);

        // Associate skill tags with the search profile if provided
        if (request.getSkillTagIds() != null) {
            for (Integer tagId : request.getSkillTagIds()) {
                entity.addSkillTag(tagId);
            }
        }

        // Persist the search profile entity to the database
        SearchProfileEntity savedEntity = searchProfileRepository.save(entity);
        log.info("Search profile created successfully with ID: {}", savedEntity.getProfileId());
        
        // Convert the saved entity back to response DTO
        return searchProfileMapper.entityToResponseDto(savedEntity);
    }


}

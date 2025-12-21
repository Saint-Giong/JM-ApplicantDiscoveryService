package rmit.saintgiong.discoveryservice.searchprofile.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rmit.saintgiong.discoveryapi.internal.service.InternalGetSearchProfileInterface;
import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryservice.searchprofile.entity.SearchProfileEntity;
import rmit.saintgiong.discoveryservice.searchprofile.mapper.SearchProfileMapper;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class GetSearchProfileService implements InternalGetSearchProfileInterface {

    private final SearchProfileRepository searchProfileRepository;
    private final SearchProfileMapper searchProfileMapper;

    /**
     * Retrieves a search profile by its unique identifier.
     * 
     * @param id the UUID of the search profile to retrieve
     * @return a {@link SearchProfileResponseDto} containing the profile's details
     *         including salary range, degree, employment types, and skill tags
     * @throws EntityNotFoundException if no search profile exists with the given ID
     */
    @Override
    @Transactional(readOnly = true)
    public SearchProfileResponseDto getSearchProfileById(UUID id) {
        log.info("Fetching search profile with ID: {}", id);
        
        // Query the database for the search profile, throw exception if not found
        SearchProfileEntity entity = searchProfileRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Search profile not found with ID: {}", id);
                    return new EntityNotFoundException("Search Profile not found with id: " + id);
                });

        log.debug("Search profile found for company: {}", entity.getCompanyId());
        
        // Convert entity to response DTO (handles BitSet -> String conversion for employment types)
        return searchProfileMapper.entityToResponseDto(entity);
    }

    /**
     * Retrieves all search profiles belonging to a specific company.
     * 
     * @param companyId the UUID of the company to retrieve profiles for
     * @return a list of {@link SearchProfileResponseDto} containing all profiles
     *         for the specified company, or an empty list if none exist
     */
    @Override
    @Transactional(readOnly = true)
    public List<SearchProfileResponseDto> getSearchProfilesByCompanyId(UUID companyId) {
        log.info("Fetching all search profiles for company: {}", companyId);
        
        // Query the database for all search profiles belonging to the company
        List<SearchProfileEntity> entities = searchProfileRepository.findByCompanyId(companyId);
        
        log.debug("Found {} search profiles for company: {}", entities.size(), companyId);
        
        // Convert each entity to response DTO
        return entities.stream()
                .map(searchProfileMapper::entityToResponseDto)
                .toList();
    }
}


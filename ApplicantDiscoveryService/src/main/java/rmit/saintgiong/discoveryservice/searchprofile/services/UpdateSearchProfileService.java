package rmit.saintgiong.discoveryservice.searchprofile.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rmit.saintgiong.discoveryapi.internal.dto.UpdateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.service.InternalUpdateSearchProfileInterface;
import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryservice.searchprofile.entity.SearchProfileEntity;
import rmit.saintgiong.discoveryservice.searchprofile.mapper.SearchProfileMapper;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UpdateSearchProfileService implements InternalUpdateSearchProfileInterface {

    private final SearchProfileRepository searchProfileRepository;
    private final SearchProfileMapper searchProfileMapper;

    /**
     * Updates an existing search profile with the provided data.
     * Only non-null fields in the request will be updated (partial update).
     *
     * @param profileId the unique identifier of the profile to update
     * @param request   the update request containing fields to modify
     * @return a {@link SearchProfileResponseDto} containing the updated profile's details
     * @throws IllegalArgumentException if profileId or request is null
     * @throws EntityNotFoundException  if no profile exists with the given ID
     */
    @Override
    @Transactional
    public SearchProfileResponseDto updateSearchProfile(UUID profileId, UpdateSearchProfileRequestDto request) {
        // Validate input parameters
        if (profileId == null) {
            throw new IllegalArgumentException("Profile ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        log.info("Updating search profile with ID: {}", profileId);

        // Find the existing profile or throw exception if not found
        SearchProfileEntity existingEntity = searchProfileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Search profile not found with ID: " + profileId));

        // Apply partial updates from the request DTO to the existing entity
        searchProfileMapper.updateEntityFromDto(request, existingEntity);

        // Update skill tags if provided in the request
        if (request.getSkillTagId() != null) {
            // Clear existing skill tags and add new ones
            existingEntity.getSkillTags().clear();
            for (Integer tagId : request.getSkillTagId()) {
                existingEntity.addSkillTag(tagId);
            }
        }

        // Persist the updated entity
        SearchProfileEntity savedEntity = searchProfileRepository.save(existingEntity);
        log.info("Search profile updated successfully with ID: {}", savedEntity.getProfileId());

        // Convert and return the updated entity as response DTO
        return searchProfileMapper.entityToResponseDto(savedEntity);
    }
}

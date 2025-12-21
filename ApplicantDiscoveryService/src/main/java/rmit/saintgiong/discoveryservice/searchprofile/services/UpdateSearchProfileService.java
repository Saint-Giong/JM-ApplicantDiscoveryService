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
     * @throws IllegalArgumentException if profileId or request is null, or if salary range is invalid
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

        // Validate salary range after partial update is applied
        // This catches cases where only salaryMin or salaryMax is updated
        validateSalaryRangeAfterUpdate(existingEntity, request);

        // Update skill tags if provided in the request
        if (request.getSkillTagIds() != null) {
            // Clear existing skill tags and add new ones
            existingEntity.getSkillTags().clear();
            for (Integer tagId : request.getSkillTagIds()) {
                existingEntity.addSkillTag(tagId);
            }
        }

        // Persist the updated entity
        SearchProfileEntity savedEntity = searchProfileRepository.save(existingEntity);
        log.info("Search profile updated successfully with ID: {}", savedEntity.getProfileId());

        // Convert and return the updated entity as response DTO
        return searchProfileMapper.entityToResponseDto(savedEntity);
    }

    /**
     * Validates that the salary range is valid after applying partial updates.
     * This handles edge cases where only salaryMin or salaryMax is updated,
     * ensuring the resulting range is still valid (min <= max).
     *
     * @param entity  the entity with updated values
     * @param request the update request (used to determine which fields were updated)
     * @throws IllegalArgumentException if salaryMin > salaryMax after the update
     */
    private void validateSalaryRangeAfterUpdate(SearchProfileEntity entity, UpdateSearchProfileRequestDto request) {
        Double salaryMin = entity.getSalaryMin();
        Double salaryMax = entity.getSalaryMax();

        // Only validate if both values exist after the update
        if (salaryMin != null && salaryMax != null && salaryMin > salaryMax) {
            String errorMessage;
            if (request.getSalaryMin() != null && request.getSalaryMax() == null) {
                // Only min was updated
                errorMessage = String.format(
                        "Updated minimum salary (%.2f) cannot be greater than existing maximum salary (%.2f)",
                        salaryMin, salaryMax);
            } else if (request.getSalaryMax() != null && request.getSalaryMin() == null) {
                // Only max was updated
                errorMessage = String.format(
                        "Updated maximum salary (%.2f) cannot be less than existing minimum salary (%.2f)",
                        salaryMax, salaryMin);
            } else {
                // Both were updated (caught by @ValidSalaryRange, but double-check here)
                errorMessage = String.format(
                        "Minimum salary (%.2f) must be less than or equal to maximum salary (%.2f)",
                        salaryMin, salaryMax);
            }
            log.warn("Salary range validation failed for profile {}: {}", entity.getProfileId(), errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        log.debug("Salary range validation passed for profile {}: min={}, max={}",
                entity.getProfileId(), salaryMin, salaryMax);
    }
}

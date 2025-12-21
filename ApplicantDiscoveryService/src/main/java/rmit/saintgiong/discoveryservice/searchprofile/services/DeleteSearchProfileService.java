package rmit.saintgiong.discoveryservice.searchprofile.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rmit.saintgiong.discoveryapi.internal.service.InternalDeleteSearchProfileInterface;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class DeleteSearchProfileService implements InternalDeleteSearchProfileInterface {

    private final SearchProfileRepository searchProfileRepository;

    /**
     * Deletes a search profile by its unique identifier.
     *
     * @param profileId the unique identifier of the profile to delete
     * @throws IllegalArgumentException if profileId is null
     * @throws EntityNotFoundException  if no profile exists with the given ID
     */
    @Override
    @Transactional
    public void deleteSearchProfile(UUID profileId) {
        // Validate input parameter
        if (profileId == null) {
            throw new IllegalArgumentException("Profile ID cannot be null");
        }

        log.info("Deleting search profile with ID: {}", profileId);

        // Verify the profile exists before attempting to delete
        if (!searchProfileRepository.existsById(profileId)) {
            throw new EntityNotFoundException("Search profile not found with ID: " + profileId);
        }

        // Delete the profile (cascades to skill tags due to orphanRemoval=true)
        searchProfileRepository.deleteById(profileId);
        log.info("Search profile deleted successfully with ID: {}", profileId);
    }
}

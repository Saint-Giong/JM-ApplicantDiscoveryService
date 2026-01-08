package rmit.saintgiong.discoveryapi.internal.service.elasticsearch;

import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface SearchingInterface {
    /**
     * Basic search by name (First Name or Last Name)
     */
    List<ApplicantDocument> searchByName(String name);

    /**
     * Advanced Full-Text Search
     * Searches across Name, Biography, and Address with City/Country filtering.
     */
    List<ApplicantDocument> searchComprehensive(String keyword, String city, String country);

    /**
     * Get all applicants with pagination
     */
    Page<ApplicantDocument> getAllApplicants(Pageable pageable);

    /**
     * Get applicant by ID
     */
    ApplicantDocument getApplicantById(UUID id);
}
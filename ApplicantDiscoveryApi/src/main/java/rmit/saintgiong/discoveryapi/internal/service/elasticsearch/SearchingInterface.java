package rmit.saintgiong.discoveryapi.internal.service.elasticsearch;

import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;

import java.util.List;

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
}
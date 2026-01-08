package rmit.saintgiong.discoveryservice.domain.services.elasticsearch; // Adjust package to match yours

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.FieldValue;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;
import rmit.saintgiong.discoveryapi.internal.service.elasticsearch.SearchingInterface;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SearchingService implements SearchingInterface {

    private static final String APPLICANTS_INDEX = "applicants";
    private final ElasticsearchOperations elasticsearchOperations;

    public SearchingService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    /**
     * Simple implementation: Matches First Name OR Last Name
     */
    @Override
    public List<ApplicantDocument> searchByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }

        // Construct a Multi-Match query
        Query query = QueryBuilders.multiMatch()
                .fields("firstName", "lastName") // Fields to search
                .query(name)                     // The text to find
                .fuzziness("AUTO")               // Handle typos (e.g., "Jon" finds "John")
                .build()
                ._toQuery();

        return executeQuery(query);
    }

    /**
     * The "Ultimo" implementation:
     * FTS on Biography/Address/Name + Filters for City/Country
     */
    @Override
    public List<ApplicantDocument> searchComprehensive(String keyword, String city, String country) {

        // 1. Create a list of "Must" conditions (Boolean Logic)
        List<Query> mustQueries = new ArrayList<>();

        // 2. Full Text Search Clause
        if (keyword != null && !keyword.isBlank()) {
            Query ftsQuery = QueryBuilders.multiMatch()
                    .fields("firstName", "lastName", "biography", "address") // Search everywhere
                    .query(keyword)
                    .fuzziness("AUTO")
                    .build()
                    ._toQuery();
            mustQueries.add(ftsQuery);
        }

        // 3. Filter Clause: City (Exact Match because it is Keyword)
        if (city != null && !city.isBlank()) {
            Query cityFilter = QueryBuilders.term()
                    .field("city")
                    .value(city)
                    .build()
                    ._toQuery();
            mustQueries.add(cityFilter);
        }

        // 4. Filter Clause: Country (Exact Match)
        if (country != null && !country.isBlank()) {
            Query countryFilter = QueryBuilders.term()
                    .field("country") // Assumes Country enum saves as String/Keyword
                    .value(country)
                    .build()
                    ._toQuery();
            mustQueries.add(countryFilter);
        }

        // 5. Combine everything into one Boolean Query
        Query finalQuery = QueryBuilders.bool()
                .must(mustQueries)
                .build()
                ._toQuery();

        return executeQuery(finalQuery);
    }

    @Override
    public Page<ApplicantDocument> getAllApplicants(Pageable pageable) {
        Query query = QueryBuilders.matchAll().build()._toQuery();
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable)
                .build();

        SearchHits<ApplicantDocument> hits = elasticsearchOperations.search(
                nativeQuery,
                ApplicantDocument.class,
                IndexCoordinates.of(APPLICANTS_INDEX)
        );

        List<ApplicantDocument> applicants = hits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(applicants, pageable, hits.getTotalHits());
    }

    @Override
    public ApplicantDocument getApplicantById(UUID id) {
        return elasticsearchOperations.get(
                id.toString(),
                ApplicantDocument.class,
                IndexCoordinates.of(APPLICANTS_INDEX)
        );
    }

    @Override
    public Page<ApplicantDocument> searchApplicants(
            String name,
            String keyword,
            String locationValue,
            boolean isCountry,
            List<String> educationLevels,
            List<Long> skillIds,
            String workExperienceType,
            Pageable pageable) {

        List<Query> mustQueries = new ArrayList<>();

        // 1. Name Search (First Name OR Last Name)
        if (name != null && !name.isBlank()) {
            Query nameQuery = QueryBuilders.multiMatch()
                    .fields("firstName", "lastName")
                    .query(name)
                    .fuzziness("AUTO")
                    .build()
                    ._toQuery();
            mustQueries.add(nameQuery);
        }

        // 2. Full Text Search (Keywords)
        // Searches in biography AND nested work experience fields AND nested education fields
        if (keyword != null && !keyword.isBlank()) {
            
            // Should clause for top-level fields
            Query topLevelMatch = QueryBuilders.multiMatch()
                    .fields("biography")
                    .query(keyword)
                    .fuzziness("AUTO")
                    .build()
                    ._toQuery();

            // Should clause for Nested Work Experience (Flattened path)
            Query nestedWorkExpMatch = QueryBuilders.multiMatch()
                    .fields("workExperienceList.position", "workExperienceList.description", "workExperienceList.companyName")
                    .query(keyword)
                    .fuzziness("AUTO")
                    .build()
                    ._toQuery();

             // Should clause for Nested Education (Flattened path)
            Query nestedEducationMatch = QueryBuilders.multiMatch()
                    .fields("educationList.institutionName", "educationList.description")
                    .query(keyword)
                    .fuzziness("AUTO")
                    .build()
                    ._toQuery();


            // Combine them with OR logic (Should) inside a MUST: (TopLevel OR NestedExp OR NestedEdu)
            Query ftsCombination = QueryBuilders.bool()
                    .should(topLevelMatch)
                    .should(nestedWorkExpMatch)
                    .should(nestedEducationMatch)
                    .minimumShouldMatch("1")
                    .build()
                    ._toQuery();

            mustQueries.add(ftsCombination);
        }

        // 2. Location Filter
        if (locationValue != null && !locationValue.isBlank()) {
            if (isCountry) {
                // Exact match on Country Code (assuming enum/mapped as keyword)
                 Query countryQuery = QueryBuilders.term()
                        .field("country")
                        .value(locationValue)
                        .caseInsensitive(true)
                        .build()
                        ._toQuery();
                mustQueries.add(countryQuery);
            } else {
                // City is a Keyword field, use Term for exact or wildcard for flexibility.
                // Requirement 5.1.3: "one value for the Location".
                // Using term with case insensitivity for better UX on Keyword fields
                Query cityQuery = QueryBuilders.term()
                        .field("city")
                        .value(locationValue)
                        .caseInsensitive(true)
                        .build()
                        ._toQuery();
                mustQueries.add(cityQuery);
            }
        }

        // 3. Education Level Filter (Nested -> Flattened for robustness)
        if (educationLevels != null && !educationLevels.isEmpty()) {
            // Use Bool Should Match to handle potentially mixed Text/Keyword mappings (case sensitivity)
             List<String> upperCaseDegrees = educationLevels.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

            List<Query> degreeQueries = upperCaseDegrees.stream()
                    .map(degree -> QueryBuilders.match()
                            .field("educationList.degree")
                            .query(degree)
                            .build()
                            ._toQuery())
                    .collect(Collectors.toList());

            Query educationQuery = QueryBuilders.bool()
                    .should(degreeQueries)
                    .minimumShouldMatch("1")
                    .build()
                    ._toQuery();

            mustQueries.add(educationQuery);
        }

        // 4. Skills Filter (IDs)
        if (skillIds != null && !skillIds.isEmpty()) {
             // "Any applicant declaring their skills... included" -> Terms query (OR logic)
            Query skillsQuery = QueryBuilders.terms()
                    .field("skillIds")
                    .terms(t -> t.value(skillIds.stream().map(FieldValue::of).collect(Collectors.toList())))
                    .build()
                    ._toQuery();
            mustQueries.add(skillsQuery);
        }

        // 5. Work Experience Type Filter
        if (workExperienceType != null) {
            if ("NONE".equalsIgnoreCase(workExperienceType)) {
                // Must NOT have workExperienceList
                // Check if field exists
                Query exists = QueryBuilders.exists().field("workExperienceList").build()._toQuery();
                Query mustNotHaveExp = QueryBuilders.bool().mustNot(exists).build()._toQuery();
                mustQueries.add(mustNotHaveExp);
            } else if ("ANY".equalsIgnoreCase(workExperienceType)) {
                 // Must HAVE workExperienceList
                 Query mustHaveExp = QueryBuilders.exists().field("workExperienceList").build()._toQuery();
                mustQueries.add(mustHaveExp);
            }
        }

        // Assemble Final Query
        Query finalQuery = QueryBuilders.bool()
                .must(mustQueries)
                .build()
                ._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(finalQuery)
                .withPageable(pageable)
                .build();

        SearchHits<ApplicantDocument> hits = elasticsearchOperations.search(
                nativeQuery,
                ApplicantDocument.class,
                IndexCoordinates.of(APPLICANTS_INDEX)
        );

        List<ApplicantDocument> applicants = hits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(applicants, pageable, hits.getTotalHits());
    }

    // Helper method to execute query and map results
    private List<ApplicantDocument> executeQuery(Query query) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .build();

        SearchHits<ApplicantDocument> hits = elasticsearchOperations.search(
                nativeQuery,
                ApplicantDocument.class,
                IndexCoordinates.of(APPLICANTS_INDEX)
        );

        return hits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }
}
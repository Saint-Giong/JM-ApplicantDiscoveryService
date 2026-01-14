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

        String targetLocation = (locationValue != null && !locationValue.isBlank()) ? locationValue : "Vietnam";
        boolean targetIsCountry = (locationValue == null || locationValue.isBlank()) || isCountry;

        if (targetIsCountry) {
            mustQueries.add(QueryBuilders.match()
                    .field("country")
                    .query(targetLocation)
                    .build()._toQuery());
        } else {
            mustQueries.add(QueryBuilders.match() // Use match for fuzziness/case-insensitivity or term for exact
                    .field("city")
                    .query(targetLocation)
                    .build()._toQuery());
        }

        if (name != null && !name.isBlank()) {
            mustQueries.add(QueryBuilders.multiMatch()
                    .fields("firstName", "lastName")
                    .query(name)
                    .fuzziness("AUTO")
                    .build()._toQuery());
        }

        if (keyword != null && !keyword.isBlank()) {
            List<Query> ftsShouldQueries = new ArrayList<>();

            ftsShouldQueries.add(QueryBuilders.multiMatch()
                    .fields("biography", "aboutMe", "skillNames")
                    .query(keyword)
                    .fuzziness("AUTO")
                    .build()._toQuery());

            Query nestedWorkExpQuery = QueryBuilders.multiMatch()
                    .fields("workExperiences.description", "workExperiences.position", "workExperiences.companyName")
                    .query(keyword)
                    .fuzziness("AUTO")
                    .build()._toQuery();
            
            ftsShouldQueries.add(QueryBuilders.nested()
                    .path("workExperiences")
                    .query(nestedWorkExpQuery)
                    .build()._toQuery());

            mustQueries.add(QueryBuilders.bool()
                    .should(ftsShouldQueries)
                    .minimumShouldMatch("1")
                    .build()._toQuery());
        }

        if (educationLevels != null && !educationLevels.isEmpty()) {
             List<Query> degreeQueries = educationLevels.stream()
                .map(degree -> QueryBuilders.match()
                        .field("educations.degree")
                        .query(degree) 
                        .build()._toQuery())
                .collect(Collectors.toList());

             Query boolDegree = QueryBuilders.bool().should(degreeQueries).minimumShouldMatch("1").build()._toQuery();

             mustQueries.add(QueryBuilders.nested()
                     .path("educations")
                     .query(boolDegree)
                     .build()._toQuery());
        }

        if (skillIds != null && !skillIds.isEmpty()) {
            List<FieldValue> values = skillIds.stream().map(FieldValue::of).collect(Collectors.toList());
            mustQueries.add(QueryBuilders.terms()
                    .field("skillIds")
                    .terms(t -> t.value(values))
                    .build()._toQuery());
        }

        if (workExperienceType != null) {
            if ("NONE".equalsIgnoreCase(workExperienceType)) {
                // No Experience
                Query nestedAny = QueryBuilders.nested()
                        .path("workExperiences")
                        .query(QueryBuilders.matchAll().build()._toQuery())
                        .build()._toQuery();
                mustQueries.add(QueryBuilders.bool().mustNot(nestedAny).build()._toQuery());
                
            } else if ("ANY".equalsIgnoreCase(workExperienceType)) {
                 // Must have at least one
                Query nestedAny = QueryBuilders.nested()
                        .path("workExperiences")
                        .query(QueryBuilders.matchAll().build()._toQuery())
                        .build()._toQuery();
                mustQueries.add(nestedAny);
            }
            
        }

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
    public void deleteIndex() {
        elasticsearchOperations.indexOps(IndexCoordinates.of(APPLICANTS_INDEX)).delete();
    }
}
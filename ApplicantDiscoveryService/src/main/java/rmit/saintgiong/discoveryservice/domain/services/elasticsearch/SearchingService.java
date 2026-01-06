package rmit.saintgiong.discoveryservice.domain.services.elasticsearch; // Adjust package to match yours

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;
import rmit.saintgiong.discoveryapi.internal.service.elasticsearch.SearchingInterface;

import java.util.ArrayList;
import java.util.List;
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
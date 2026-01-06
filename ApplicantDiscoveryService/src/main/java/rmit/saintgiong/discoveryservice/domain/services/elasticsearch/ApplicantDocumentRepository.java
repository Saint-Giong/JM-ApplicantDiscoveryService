package rmit.saintgiong.discoveryservice.domain.services.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicantDocumentRepository extends ElasticsearchRepository<ApplicantDocument, UUID> {

    @Query("""
        {
            "bool": {
            "should": [
                { "wildcard": { "firstName": { "value": "*?0*", "case_insensitive": true } } },
                { "wildcard": { "lastName": { "value": "*?0*", "case_insensitive": true } } }
            ],
            "minimum_should_match": 1
            }
        }
        """)
    List<ApplicantDocument> findByName(String name);
}

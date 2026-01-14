package rmit.saintgiong.discoveryservice.domain.services.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import rmit.saintgiong.discoveryapi.internal.service.elasticsearch.IndexingInterface;
import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;

@Service
public class IndexingService implements IndexingInterface {

    private static final Logger log = LoggerFactory.getLogger(IndexingService.class);
    private static final String APPLICANTS_INDEX = "applicants";

    private final ElasticsearchOperations elasticsearchOperations;

    public IndexingService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public void indexApplicant(ApplicantDocument document) {
        try {
            // Ensure index exists with correct mapping before indexing
            ensureIndexExists();

            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withId(document.applicantId().toString())
                    .withObject(document)
                    .build();

            elasticsearchOperations.index(indexQuery, IndexCoordinates.of(APPLICANTS_INDEX));
            log.info("Indexed applicant {}", document.applicantId());

        } catch (Exception e) {
            log.error("Failed to index applicant: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to index applicant", e);
        }
    }

    /**
     * Ensures the index exists with the correct mapping derived from ApplicantDocument annotations.
     * This is critical for nested fields like 'educations' and 'workExperiences' to work correctly.
     */
    private void ensureIndexExists() {
        var indexOps = elasticsearchOperations.indexOps(ApplicantDocument.class);
        if (!indexOps.exists()) {
            log.info("Creating index '{}' with mapping from ApplicantDocument annotations", APPLICANTS_INDEX);
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping());
            log.info("Index '{}' created successfully with correct nested mapping", APPLICANTS_INDEX);
        }
    }

    @Override
    public void deleteApplicant(String applicantId) {
        try {
            elasticsearchOperations.delete(applicantId, IndexCoordinates.of(APPLICANTS_INDEX));
            log.info("Deleted applicant {}", applicantId);
        } catch (Exception e) {
            log.error("Failed to delete applicant: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete applicant", e);
        }
    }
}
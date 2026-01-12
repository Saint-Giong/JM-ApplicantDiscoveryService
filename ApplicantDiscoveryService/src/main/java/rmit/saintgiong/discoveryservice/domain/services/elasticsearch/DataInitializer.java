package rmit.saintgiong.discoveryservice.domain.services.elasticsearch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;
import rmit.saintgiong.discoveryapi.internal.document.Country;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final IndexingService indexingService;
    private final ObjectMapper objectMapper;

    public DataInitializer(IndexingService indexingService, ObjectMapper objectMapper) {
        this.indexingService = indexingService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadMockData() {
        logger.info("Starting mock data loading...");

        try {
//            loadCountries();
            loadApplicants();


            logger.info("Mock data loading completed successfully!");
        } catch (Exception e) {
            logger.error("Error loading mock data", e);
        }
    }

//    private void loadCountries() throws IOException {
//        logger.info("Loading countries from JSON...");
//
//        InputStream inputStream = new ClassPathResource("mock/countries.json").getInputStream();
//        List<Country> countries = objectMapper.readValue(
//                inputStream,
//                new TypeReference<List<Country>>() {
//                });
//
//        Country.initialize(countries);
//        logger.info("Loaded {} countries", countries.size());
//    }


    private void loadApplicants() throws IOException {
        logger.info("Loading mock applicants...");

        InputStream inputStream = new ClassPathResource("mock/mock-applicants.json").getInputStream();
        List<ApplicantDocument> applicants = objectMapper.readValue(
                inputStream,
                new TypeReference<List<ApplicantDocument>>() {
                });

        for (ApplicantDocument applicant : applicants) {
            indexingService.indexApplicant(applicant);
        }
        logger.info("Loaded {} applicants", applicants.size());
    }
}

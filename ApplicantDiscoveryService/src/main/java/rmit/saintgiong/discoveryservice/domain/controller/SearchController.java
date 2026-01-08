package rmit.saintgiong.discoveryservice.domain.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;
import rmit.saintgiong.discoveryapi.internal.service.elasticsearch.SearchingInterface;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@RestController
public class SearchController {

    private final SearchingInterface searchingInterface;

    public SearchController(SearchingInterface searchingInterface) {
        this.searchingInterface = searchingInterface;
    }

    // Test: GET /search/name?q=John
    @GetMapping("search/name")
    public ResponseEntity<List<ApplicantDocument>> searchByName(@RequestParam String q) {
        return ResponseEntity.ok(searchingInterface.searchByName(q));
    }

    // Test: GET /search/advanced?q=engineer&city=San Francisco
    @GetMapping("search/advanced")
    public ResponseEntity<List<ApplicantDocument>> searchAdvanced(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country) {
        return ResponseEntity.ok(searchingInterface.searchComprehensive(q, city, country));
    }

    @GetMapping("applicants/all")
    public ResponseEntity<Page<ApplicantDocument>> getAllApplicants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(searchingInterface.getAllApplicants(pageable));
    }

    @GetMapping("applicants/{applicantId}")
    public ResponseEntity<ApplicantDocument> getApplicantById(@PathVariable UUID applicantId) {
        return ResponseEntity.ok(searchingInterface.getApplicantById(applicantId));
    }
}
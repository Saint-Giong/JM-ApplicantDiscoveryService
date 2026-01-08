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

    @GetMapping("applicants/search")
    public ResponseEntity<Page<ApplicantDocument>> searchApplicants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "false") boolean isCountry,
            @RequestParam(required = false) List<String> education,
            @RequestParam(required = false) List<Long> skills,
            @RequestParam(required = false) String experienceType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(searchingInterface.searchApplicants(
                name, keyword, location, isCountry, education, skills, experienceType, pageable));
    }
}
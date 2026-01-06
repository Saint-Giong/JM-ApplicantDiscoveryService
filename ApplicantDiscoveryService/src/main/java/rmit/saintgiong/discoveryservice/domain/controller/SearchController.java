package rmit.saintgiong.discoveryservice.domain.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rmit.saintgiong.discoveryapi.internal.document.ApplicantDocument;
import rmit.saintgiong.discoveryapi.internal.service.elasticsearch.SearchingInterface;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchingInterface searchingInterface;

    public SearchController(SearchingInterface searchingInterface) {
        this.searchingInterface = searchingInterface;
    }

    // Test: GET /search/name?q=John
    @GetMapping("/name")
    public List<ApplicantDocument> searchByName(@RequestParam String q) {
        return searchingInterface.searchByName(q);
    }

    // Test: GET /search/advanced?q=engineer&city=San Francisco
    @GetMapping("/advanced")
    public List<ApplicantDocument> searchAdvanced(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country) {
        return searchingInterface.searchComprehensive(q, city, country);
    }
}
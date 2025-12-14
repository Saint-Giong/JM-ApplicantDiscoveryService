package rmit.saintgiong.discoveryservice.searchprofile.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rmit.saintgiong.discoveryapi.internal.dto.CreateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryservice.searchprofile.services.CreateSearchProfileService;
import rmit.saintgiong.discoveryservice.searchprofile.services.DeleteSearchProfileService;
import rmit.saintgiong.discoveryservice.searchprofile.services.GetSearchProfileService;
import rmit.saintgiong.discoveryservice.searchprofile.services.UpdateSearchProfileService;

@RestController
@AllArgsConstructor
@Tag(name = "Search Profile", description = "APIs for managing applicant search profiles")
public class SearchProfileController {

    private final CreateSearchProfileService createSearchProfileService;
    private final GetSearchProfileService getSearchProfileService;
    private final UpdateSearchProfileService updateSearchProfileService;
    private final DeleteSearchProfileService deleteSearchProfileService;

    @PostMapping("/search-profile")
    public ResponseEntity<SearchProfileResponseDto> createSearchProfile(
            @Valid @RequestBody CreateSearchProfileRequestDto request) {
        SearchProfileResponseDto createdProfile = createSearchProfileService.createSearchProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
    }

}


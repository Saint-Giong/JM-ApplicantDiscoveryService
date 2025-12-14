package rmit.saintgiong.discoveryservice.searchprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rmit.saintgiong.discoveryapi.internal.dto.CreateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryservice.common.dto.ErrorResponseDto;
import rmit.saintgiong.discoveryservice.searchprofile.services.CreateSearchProfileService;
import rmit.saintgiong.discoveryservice.searchprofile.services.DeleteSearchProfileService;
import rmit.saintgiong.discoveryservice.searchprofile.services.GetSearchProfileService;
import rmit.saintgiong.discoveryservice.searchprofile.services.UpdateSearchProfileService;

import java.util.UUID;

@RestController
@AllArgsConstructor
@Tag(name = "Search Profile", description = "APIs for managing applicant search profiles")
public class SearchProfileController { //TODO: Check if the user is premium account

    private final CreateSearchProfileService createSearchProfileService;
    private final GetSearchProfileService getSearchProfileService;
    private final UpdateSearchProfileService updateSearchProfileService;
    private final DeleteSearchProfileService deleteSearchProfileService;

    /**
     * Creates a new search profile for applicant discovery.
     * Allows companies to define criteria for finding suitable applicants.
     */
    @Operation(
            summary = "Create a search profile",
            description = "Creates a new search profile with specified criteria including salary range, " +
                    "degree requirements, employment types, location, and skill tags."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Search profile created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SearchProfileResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/search-profile")
    public ResponseEntity<SearchProfileResponseDto> createSearchProfile(
            @Valid @RequestBody CreateSearchProfileRequestDto request) {
        //TODO: for regular company user, company id should be from access token instead of request body
        SearchProfileResponseDto createdProfile = createSearchProfileService.createSearchProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
    }

    /**
     * Retrieves a search profile by its unique identifier.
     */
    @Operation(
            summary = "Get a search profile by ID",
            description = "Retrieves the details of a specific search profile using its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search profile retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SearchProfileResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Search profile not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/search-profile/{id}")
    public ResponseEntity<SearchProfileResponseDto> getSearchProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(getSearchProfileService.getSearchProfileById(id));
    }

}


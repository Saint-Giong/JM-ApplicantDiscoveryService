package rmit.saintgiong.discoveryservice.domain.controller;

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
import rmit.saintgiong.discoveryapi.internal.common.dto.CreateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.common.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryapi.internal.common.dto.UpdateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.common.dto.common.ApiResponseDto;
import rmit.saintgiong.discoveryservice.domain.services.CreateSearchProfileService;
import rmit.saintgiong.discoveryservice.domain.services.DeleteSearchProfileService;
import rmit.saintgiong.discoveryservice.domain.services.GetSearchProfileService;
import rmit.saintgiong.discoveryservice.domain.services.UpdateSearchProfileService;
import rmit.saintgiong.shared.response.ErrorResponseDto;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

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
                            schema = @Schema(implementation = ApiResponseDto.class)
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
    public Callable<ResponseEntity<ApiResponseDto<SearchProfileResponseDto>>> createSearchProfile(
            @Valid @RequestBody CreateSearchProfileRequestDto request) {
        return () -> {
            //TODO: for regular company user, company id should be from access token instead of request body
            SearchProfileResponseDto createdProfile = createSearchProfileService.createSearchProfile(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success(createdProfile, "Search profile created successfully"));
        };
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
                            schema = @Schema(implementation = ApiResponseDto.class)
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
    public Callable<ResponseEntity<ApiResponseDto<SearchProfileResponseDto>>> getSearchProfile(@PathVariable UUID id) {
        return () -> {
            SearchProfileResponseDto profile = getSearchProfileService.getSearchProfileById(id);
            return ResponseEntity.ok(ApiResponseDto.success(profile, "Search profile retrieved successfully"));
        };
    }

    /**
     * Retrieves all search profiles belonging to a specific company.
     */
    @Operation(
            summary = "Get all search profiles by company ID",
            description = "Retrieves all search profiles that belong to a specific company using the company's unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search profiles retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            )
    })
    @GetMapping("/search-profile/company/{companyId}")
    public Callable<ResponseEntity<ApiResponseDto<List<SearchProfileResponseDto>>>> getSearchProfilesByCompanyId(
            @PathVariable UUID companyId) {
        return () -> {
            List<SearchProfileResponseDto> profiles = getSearchProfileService.getSearchProfilesByCompanyId(companyId);
            return ResponseEntity.ok(ApiResponseDto.success(profiles, "Search profiles retrieved successfully"));
        };
    }

    /**
     * Updates an existing search profile with the provided data.
     * Only non-null fields will be modified.
     */
    @Operation(
            summary = "Update a search profile",
            description = "Updates an existing search profile with the provided data. " +
                    "Only non-null fields in the request will be modified."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search profile updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
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
    @PutMapping("/search-profile/{id}")
    public Callable<ResponseEntity<ApiResponseDto<SearchProfileResponseDto>>> updateSearchProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSearchProfileRequestDto request) {
        return () -> {
            SearchProfileResponseDto updatedProfile = updateSearchProfileService.updateSearchProfile(id, request);
            return ResponseEntity.ok(ApiResponseDto.success(updatedProfile, "Search profile updated successfully"));
        };
    }

    /**
     * Deletes a search profile by its unique identifier.
     */
    @Operation(
            summary = "Delete a search profile",
            description = "Permanently deletes a search profile and all associated skill tags using its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search profile deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
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
    @DeleteMapping("/search-profile/{id}")
    public Callable<ResponseEntity<ApiResponseDto<Void>>> deleteSearchProfile(@PathVariable UUID id) {
        return () -> {
            deleteSearchProfileService.deleteSearchProfile(id);
            return ResponseEntity.ok(ApiResponseDto.success(null, "Search profile deleted successfully"));
        };
    }

}


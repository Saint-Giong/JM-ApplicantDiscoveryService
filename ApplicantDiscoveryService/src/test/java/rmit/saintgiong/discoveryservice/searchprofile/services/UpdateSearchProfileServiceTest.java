package rmit.saintgiong.discoveryservice.searchprofile.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryapi.internal.dto.UpdateSearchProfileRequestDto;
import rmit.saintgiong.discoveryservice.common.degree.type.DegreeType;
import rmit.saintgiong.discoveryservice.searchprofile.entity.SearchProfileEntity;
import rmit.saintgiong.discoveryservice.searchprofile.entity.SearchProfile_SkillTagEntity;
import rmit.saintgiong.discoveryservice.searchprofile.mapper.SearchProfileMapper;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UpdateSearchProfileService.
 * Tests the business logic for updating search profiles.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateSearchProfileService Tests")
class UpdateSearchProfileServiceTest {

    @Mock
    private SearchProfileRepository searchProfileRepository;

    @Mock
    private SearchProfileMapper searchProfileMapper;

    @InjectMocks
    private UpdateSearchProfileService updateSearchProfileService;

    private UUID companyId;
    private UUID profileId;
    private UpdateSearchProfileRequestDto requestDto;
    private SearchProfileEntity existingEntity;
    private SearchProfileEntity savedEntity;
    private SearchProfileResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // Initialize test data
        companyId = UUID.randomUUID();
        profileId = UUID.randomUUID();

        // Build update request DTO
        requestDto = UpdateSearchProfileRequestDto.builder()
                .salaryMin(60000.0)
                .salaryMax(120000.0)
                .highestDegree("MASTER")
                .employmentTypes(Set.of("FULL_TIME", "CONTRACT"))
                .country("Singapore")
                .companyId(companyId)
                .skillTagId(Set.of(4, 5, 6))
                .build();

        // Build existing entity (before update)
        existingEntity = SearchProfileEntity.builder()
                .profileId(profileId)
                .salaryMin(50000.0)
                .salaryMax(100000.0)
                .highestDegree(DegreeType.BACHELOR)
                .employmentType(new BitSet())
                .country("Vietnam")
                .companyId(companyId)
                .skillTags(new HashSet<>())
                .build();

        // Build saved entity (after update)
        savedEntity = SearchProfileEntity.builder()
                .profileId(profileId)
                .salaryMin(60000.0)
                .salaryMax(120000.0)
                .highestDegree(DegreeType.MASTER)
                .employmentType(new BitSet())
                .country("Singapore")
                .companyId(companyId)
                .skillTags(new HashSet<>())
                .build();

        // Build response DTO
        responseDto = SearchProfileResponseDto.builder()
                .profileId(profileId)
                .salaryMin(60000.0)
                .salaryMax(120000.0)
                .highestDegree("MASTER")
                .employmentTypes(Set.of("FULL_TIME", "CONTRACT"))
                .country("Singapore")
                .companyId(companyId)
                .skillTagIds(Set.of(4, 5, 6))
                .build();
    }

    @Nested
    @DisplayName("updateSearchProfile()")
    class UpdateSearchProfile {

        @Test
        @DisplayName("Should update search profile successfully with all fields")
        void shouldUpdateSearchProfileSuccessfully() {
            // Arrange
            when(searchProfileRepository.findById(profileId)).thenReturn(Optional.of(existingEntity));
            doNothing().when(searchProfileMapper).updateEntityFromDto(eq(requestDto), any(SearchProfileEntity.class));
            when(searchProfileRepository.save(any(SearchProfileEntity.class))).thenReturn(savedEntity);
            when(searchProfileMapper.entityToResponseDto(savedEntity)).thenReturn(responseDto);

            // Act
            SearchProfileResponseDto result = updateSearchProfileService.updateSearchProfile(profileId, requestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getProfileId()).isEqualTo(profileId);
            assertThat(result.getSalaryMin()).isEqualTo(60000.0);
            assertThat(result.getSalaryMax()).isEqualTo(120000.0);
            assertThat(result.getHighestDegree()).isEqualTo("MASTER");
            assertThat(result.getCountry()).isEqualTo("Singapore");

            // Verify interactions
            verify(searchProfileRepository).findById(profileId);
            verify(searchProfileMapper).updateEntityFromDto(eq(requestDto), any(SearchProfileEntity.class));
            verify(searchProfileRepository).save(any(SearchProfileEntity.class));
            verify(searchProfileMapper).entityToResponseDto(savedEntity);
        }

        @Test
        @DisplayName("Should update search profile with partial fields (salary only)")
        void shouldUpdateSearchProfileWithPartialFields() {
            // Arrange - request with only salary fields
            UpdateSearchProfileRequestDto partialRequest = UpdateSearchProfileRequestDto.builder()
                    .salaryMin(70000.0)
                    .salaryMax(150000.0)
                    .companyId(companyId)
                    .build();

            SearchProfileResponseDto partialResponse = SearchProfileResponseDto.builder()
                    .profileId(profileId)
                    .salaryMin(70000.0)
                    .salaryMax(150000.0)
                    .highestDegree("BACHELOR") // Unchanged
                    .country("Vietnam") // Unchanged
                    .companyId(companyId)
                    .build();

            when(searchProfileRepository.findById(profileId)).thenReturn(Optional.of(existingEntity));
            doNothing().when(searchProfileMapper).updateEntityFromDto(eq(partialRequest), any(SearchProfileEntity.class));
            when(searchProfileRepository.save(any(SearchProfileEntity.class))).thenReturn(existingEntity);
            when(searchProfileMapper.entityToResponseDto(existingEntity)).thenReturn(partialResponse);

            // Act
            SearchProfileResponseDto result = updateSearchProfileService.updateSearchProfile(profileId, partialRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getProfileId()).isEqualTo(profileId);
            assertThat(result.getSalaryMin()).isEqualTo(70000.0);

            // Verify
            verify(searchProfileRepository).findById(profileId);
            verify(searchProfileMapper).updateEntityFromDto(eq(partialRequest), any(SearchProfileEntity.class));
            verify(searchProfileRepository).save(any(SearchProfileEntity.class));
        }

        @Test
        @DisplayName("Should update search profile without skill tags when not provided")
        void shouldUpdateSearchProfileWithoutSkillTagsWhenNotProvided() {
            // Arrange - request without skill tags
            UpdateSearchProfileRequestDto requestWithoutSkills = UpdateSearchProfileRequestDto.builder()
                    .salaryMin(60000.0)
                    .companyId(companyId)
                    .skillTagId(null)
                    .build();

            when(searchProfileRepository.findById(profileId)).thenReturn(Optional.of(existingEntity));
            doNothing().when(searchProfileMapper).updateEntityFromDto(eq(requestWithoutSkills), any(SearchProfileEntity.class));
            when(searchProfileRepository.save(any(SearchProfileEntity.class))).thenReturn(savedEntity);
            when(searchProfileMapper.entityToResponseDto(savedEntity)).thenReturn(responseDto);

            // Act
            SearchProfileResponseDto result = updateSearchProfileService.updateSearchProfile(profileId, requestWithoutSkills);

            // Assert
            assertThat(result).isNotNull();

            // Verify skill tags were not cleared (since skillTagId is null)
            verify(searchProfileRepository).findById(profileId);
            verify(searchProfileRepository).save(any(SearchProfileEntity.class));
        }

        @Test
        @DisplayName("Should replace skill tags when provided in request")
        void shouldReplaceSkillTagsWhenProvided() {
            // Arrange - entity with existing skill tags
            Set<SearchProfile_SkillTagEntity> existingSkillTags = new HashSet<>();
            existingEntity.setSkillTags(existingSkillTags);

            when(searchProfileRepository.findById(profileId)).thenReturn(Optional.of(existingEntity));
            doNothing().when(searchProfileMapper).updateEntityFromDto(eq(requestDto), any(SearchProfileEntity.class));
            when(searchProfileRepository.save(any(SearchProfileEntity.class))).thenReturn(savedEntity);
            when(searchProfileMapper.entityToResponseDto(savedEntity)).thenReturn(responseDto);

            // Act
            SearchProfileResponseDto result = updateSearchProfileService.updateSearchProfile(profileId, requestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getSkillTagIds()).containsExactlyInAnyOrder(4, 5, 6);

            // Verify
            verify(searchProfileRepository).save(any(SearchProfileEntity.class));
        }
    }

    @Nested
    @DisplayName("Negative Tests - updateSearchProfile()")
    class NegativeTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when profileId is null")
        void shouldThrowExceptionWhenProfileIdIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> updateSearchProfileService.updateSearchProfile(null, requestDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Profile ID cannot be null");

            // Verify no repository interaction
            verifyNoInteractions(searchProfileRepository);
            verifyNoInteractions(searchProfileMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when request is null")
        void shouldThrowExceptionWhenRequestIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> updateSearchProfileService.updateSearchProfile(profileId, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Request cannot be null");

            // Verify no repository interaction
            verifyNoInteractions(searchProfileRepository);
            verifyNoInteractions(searchProfileMapper);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when profile does not exist")
        void shouldThrowExceptionWhenProfileNotFound() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();
            when(searchProfileRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> updateSearchProfileService.updateSearchProfile(nonExistentId, requestDto))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Search profile not found with ID: " + nonExistentId);

            // Verify
            verify(searchProfileRepository).findById(nonExistentId);
            verify(searchProfileRepository, never()).save(any());
            verifyNoInteractions(searchProfileMapper);
        }

        @Test
        @DisplayName("Should throw exception when repository save fails")
        void shouldThrowExceptionWhenRepositorySaveFails() {
            // Arrange
            when(searchProfileRepository.findById(profileId)).thenReturn(Optional.of(existingEntity));
            doNothing().when(searchProfileMapper).updateEntityFromDto(eq(requestDto), any(SearchProfileEntity.class));
            when(searchProfileRepository.save(any(SearchProfileEntity.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> updateSearchProfileService.updateSearchProfile(profileId, requestDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");

            // Verify
            verify(searchProfileRepository).findById(profileId);
            verify(searchProfileRepository).save(any(SearchProfileEntity.class));
        }

        @Test
        @DisplayName("Should throw exception when mapper fails")
        void shouldThrowExceptionWhenMapperFails() {
            // Arrange
            when(searchProfileRepository.findById(profileId)).thenReturn(Optional.of(existingEntity));
            doThrow(new RuntimeException("Mapping error"))
                    .when(searchProfileMapper).updateEntityFromDto(eq(requestDto), any(SearchProfileEntity.class));

            // Act & Assert
            assertThatThrownBy(() -> updateSearchProfileService.updateSearchProfile(profileId, requestDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Mapping error");

            // Verify
            verify(searchProfileRepository).findById(profileId);
            verify(searchProfileRepository, never()).save(any());
        }
    }
}

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
import rmit.saintgiong.discoveryapi.internal.dto.CreateSearchProfileRequestDto;
import rmit.saintgiong.discoveryapi.internal.dto.SearchProfileResponseDto;
import rmit.saintgiong.discoveryservice.common.degree.type.DegreeType;
import rmit.saintgiong.discoveryservice.searchprofile.entity.SearchProfileEntity;
import rmit.saintgiong.discoveryservice.searchprofile.mapper.SearchProfileMapper;

import java.util.BitSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateSearchProfileService.
 * Tests the business logic for creating search profiles.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateSearchProfileService Tests")
class CreateSearchProfileServiceTest {

    @Mock
    private SearchProfileRepository searchProfileRepository;

    @Mock
    private SearchProfileMapper searchProfileMapper;

    @InjectMocks
    private CreateSearchProfileService createSearchProfileService;

    private UUID companyId;
    private UUID profileId;
    private CreateSearchProfileRequestDto requestDto;
    private SearchProfileEntity entity;
    private SearchProfileEntity savedEntity;
    private SearchProfileResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // Initialize test data
        companyId = UUID.randomUUID();
        profileId = UUID.randomUUID();

        // Build request DTO
        requestDto = CreateSearchProfileRequestDto.builder()
                .salaryMin(50000.0)
                .salaryMax(100000.0)
                .highestDegree("BACHELOR")
                .employmentTypes(Set.of("FULL_TIME", "CONTRACT"))
                .country("Vietnam")
                .companyId(companyId)
                .skillTagIds(Set.of(1, 2, 3))
                .build();

        // Build entity (before save - no profileId)
        entity = SearchProfileEntity.builder()
                .salaryMin(50000.0)
                .salaryMax(100000.0)
                .highestDegree(DegreeType.BACHELOR)
                .employmentType(new BitSet())
                .country("Vietnam")
                .companyId(companyId)
                .build();

        // Build saved entity (after save - with profileId)
        savedEntity = SearchProfileEntity.builder()
                .profileId(profileId)
                .salaryMin(50000.0)
                .salaryMax(100000.0)
                .highestDegree(DegreeType.BACHELOR)
                .employmentType(new BitSet())
                .country("Vietnam")
                .companyId(companyId)
                .build();

        // Build response DTO
        responseDto = SearchProfileResponseDto.builder()
                .profileId(profileId)
                .salaryMin(50000.0)
                .salaryMax(100000.0)
                .highestDegree("BACHELOR")
                .employmentTypes(Set.of("FULL_TIME", "CONTRACT"))
                .country("Vietnam")
                .companyId(companyId)
                .skillTagIds(Set.of(1, 2, 3))
                .build();
    }

    @Nested
    @DisplayName("createSearchProfile()")
    class CreateSearchProfile {

        @Test
        @DisplayName("Should create search profile successfully with all fields")
        void shouldCreateSearchProfileSuccessfully() {
            // Arrange
            when(searchProfileMapper.requestDtoToEntity(requestDto)).thenReturn(entity);
            when(searchProfileRepository.save(any(SearchProfileEntity.class))).thenReturn(savedEntity);
            when(searchProfileMapper.entityToResponseDto(savedEntity)).thenReturn(responseDto);

            // Act
            SearchProfileResponseDto result = createSearchProfileService.createSearchProfile(requestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getProfileId()).isEqualTo(profileId);
            assertThat(result.getCompanyId()).isEqualTo(companyId);
            assertThat(result.getSalaryMin()).isEqualTo(50000.0);
            assertThat(result.getSalaryMax()).isEqualTo(100000.0);
            assertThat(result.getHighestDegree()).isEqualTo("BACHELOR");
            assertThat(result.getCountry()).isEqualTo("Vietnam");

            // Verify interactions
            verify(searchProfileMapper).requestDtoToEntity(requestDto);
            verify(searchProfileRepository).save(any(SearchProfileEntity.class));
            verify(searchProfileMapper).entityToResponseDto(savedEntity);
        }

        @Test
        @DisplayName("Should create search profile without skill tags")
        void shouldCreateSearchProfileWithoutSkillTags() {
            // Arrange - request without skill tags
            CreateSearchProfileRequestDto requestWithoutSkills = CreateSearchProfileRequestDto.builder()
                    .salaryMin(50000.0)
                    .salaryMax(100000.0)
                    .companyId(companyId)
                    .skillTagIds(null)
                    .build();

            when(searchProfileMapper.requestDtoToEntity(requestWithoutSkills)).thenReturn(entity);
            when(searchProfileRepository.save(any(SearchProfileEntity.class))).thenReturn(savedEntity);
            when(searchProfileMapper.entityToResponseDto(savedEntity)).thenReturn(responseDto);

            // Act
            SearchProfileResponseDto result = createSearchProfileService.createSearchProfile(requestWithoutSkills);

            // Assert
            assertThat(result).isNotNull();
            verify(searchProfileRepository).save(any(SearchProfileEntity.class));
        }

        @Test
        @DisplayName("Should create search profile with empty skill tags")
        void shouldCreateSearchProfileWithEmptySkillTags() {
            // Arrange - request with empty skill tags
            CreateSearchProfileRequestDto requestWithEmptySkills = CreateSearchProfileRequestDto.builder()
                    .salaryMin(50000.0)
                    .salaryMax(100000.0)
                    .companyId(companyId)
                    .skillTagIds(Set.of())
                    .build();

            when(searchProfileMapper.requestDtoToEntity(requestWithEmptySkills)).thenReturn(entity);
            when(searchProfileRepository.save(any(SearchProfileEntity.class))).thenReturn(savedEntity);
            when(searchProfileMapper.entityToResponseDto(savedEntity)).thenReturn(responseDto);

            // Act
            SearchProfileResponseDto result = createSearchProfileService.createSearchProfile(requestWithEmptySkills);

            // Assert
            assertThat(result).isNotNull();
            verify(searchProfileRepository).save(any(SearchProfileEntity.class));
        }

        @Test
        @DisplayName("Should add skill tags to entity when provided")
        void shouldAddSkillTagsToEntity() {
            // Arrange
            SearchProfileEntity spyEntity = spy(entity);
            when(searchProfileMapper.requestDtoToEntity(requestDto)).thenReturn(spyEntity);
            when(searchProfileRepository.save(any(SearchProfileEntity.class))).thenReturn(savedEntity);
            when(searchProfileMapper.entityToResponseDto(savedEntity)).thenReturn(responseDto);

            // Act
            createSearchProfileService.createSearchProfile(requestDto);

            // Assert - verify addSkillTag was called for each skill tag ID
            verify(spyEntity, times(3)).addSkillTag(anyInt());
        }
    }

    @Nested
    @DisplayName("Negative Tests - createSearchProfile()")
    class CreateSearchProfileNegativeTests {

        @Test
        @DisplayName("Should throw exception when mapper fails to convert DTO")
        void shouldThrowExceptionWhenMapperFails() {
            // Arrange
            when(searchProfileMapper.requestDtoToEntity(requestDto))
                    .thenThrow(new RuntimeException("Mapping failed"));

            // Act & Assert
            assertThatThrownBy(() -> createSearchProfileService.createSearchProfile(requestDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Mapping failed");

            // Verify repository was never called
            verify(searchProfileRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when repository save fails")
        void shouldThrowExceptionWhenRepositorySaveFails() {
            // Arrange
            when(searchProfileMapper.requestDtoToEntity(requestDto)).thenReturn(entity);
            when(searchProfileRepository.save(any(SearchProfileEntity.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            // Act & Assert
            assertThatThrownBy(() -> createSearchProfileService.createSearchProfile(requestDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Database connection failed");

            // Verify mapper was called but entityToResponseDto was not
            verify(searchProfileMapper).requestDtoToEntity(requestDto);
            verify(searchProfileMapper, never()).entityToResponseDto(any());
        }

        @Test
        @DisplayName("Should throw exception when response mapping fails")
        void shouldThrowExceptionWhenResponseMappingFails() {
            // Arrange
            when(searchProfileMapper.requestDtoToEntity(requestDto)).thenReturn(entity);
            when(searchProfileRepository.save(any(SearchProfileEntity.class))).thenReturn(savedEntity);
            when(searchProfileMapper.entityToResponseDto(savedEntity))
                    .thenThrow(new RuntimeException("Response mapping failed"));

            // Act & Assert
            assertThatThrownBy(() -> createSearchProfileService.createSearchProfile(requestDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Response mapping failed");
        }

        @Test
        @DisplayName("Should handle null request gracefully")
        void shouldHandleNullRequest() {
            // Act & Assert - service throws exception before calling mapper
            assertThatThrownBy(() -> createSearchProfileService.createSearchProfile(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Request cannot be null");

            // Verify mapper was never called
            verify(searchProfileMapper, never()).requestDtoToEntity(any());
            verify(searchProfileRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should propagate DataIntegrityViolationException on duplicate")
        void shouldPropagateDataIntegrityViolationException() {
            // Arrange
            when(searchProfileMapper.requestDtoToEntity(requestDto)).thenReturn(entity);
            when(searchProfileRepository.save(any(SearchProfileEntity.class)))
                    .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Duplicate entry"));

            // Act & Assert
            assertThatThrownBy(() -> createSearchProfileService.createSearchProfile(requestDto))
                    .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class)
                    .hasMessageContaining("Duplicate entry");
        }
    }
}

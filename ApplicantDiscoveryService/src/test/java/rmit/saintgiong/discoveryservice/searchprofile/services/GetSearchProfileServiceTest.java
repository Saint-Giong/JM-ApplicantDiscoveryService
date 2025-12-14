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
import rmit.saintgiong.discoveryservice.common.degree.type.DegreeType;
import rmit.saintgiong.discoveryservice.searchprofile.entity.SearchProfileEntity;
import rmit.saintgiong.discoveryservice.searchprofile.mapper.SearchProfileMapper;

import java.util.BitSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GetSearchProfileService.
 * Tests the business logic for retrieving search profiles.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetSearchProfileService Tests")
class GetSearchProfileServiceTest {

    @Mock
    private SearchProfileRepository searchProfileRepository;

    @Mock
    private SearchProfileMapper searchProfileMapper;

    @InjectMocks
    private GetSearchProfileService getSearchProfileService;

    private UUID companyId;
    private UUID profileId;
    private SearchProfileEntity entity;
    private SearchProfileResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // Initialize test data
        companyId = UUID.randomUUID();
        profileId = UUID.randomUUID();

        // Build entity
        entity = SearchProfileEntity.builder()
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
    @DisplayName("getSearchProfileById()")
    class GetSearchProfileById {

        @Test
        @DisplayName("Should return search profile when found")
        void shouldReturnSearchProfileWhenFound() {
            // Arrange
            when(searchProfileRepository.findById(profileId)).thenReturn(Optional.of(entity));
            when(searchProfileMapper.entityToResponseDto(entity)).thenReturn(responseDto);

            // Act
            SearchProfileResponseDto result = getSearchProfileService.getSearchProfileById(profileId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getProfileId()).isEqualTo(profileId);
            assertThat(result.getCompanyId()).isEqualTo(companyId);
            assertThat(result.getSalaryMin()).isEqualTo(50000.0);
            assertThat(result.getSalaryMax()).isEqualTo(100000.0);
            assertThat(result.getHighestDegree()).isEqualTo("BACHELOR");
            assertThat(result.getCountry()).isEqualTo("Vietnam");
            assertThat(result.getEmploymentTypes()).containsExactlyInAnyOrder("FULL_TIME", "CONTRACT");
            assertThat(result.getSkillTagIds()).containsExactlyInAnyOrder(1, 2, 3);

            // Verify interactions
            verify(searchProfileRepository).findById(profileId);
            verify(searchProfileMapper).entityToResponseDto(entity);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when profile not found")
        void shouldThrowEntityNotFoundExceptionWhenNotFound() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();
            when(searchProfileRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> getSearchProfileService.getSearchProfileById(nonExistentId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Search Profile not found with id: " + nonExistentId);

            // Verify repository was called but mapper was not
            verify(searchProfileRepository).findById(nonExistentId);
            verify(searchProfileMapper, never()).entityToResponseDto(any());
        }

        @Test
        @DisplayName("Should call repository with correct profile ID")
        void shouldCallRepositoryWithCorrectId() {
            // Arrange
            when(searchProfileRepository.findById(profileId)).thenReturn(Optional.of(entity));
            when(searchProfileMapper.entityToResponseDto(entity)).thenReturn(responseDto);

            // Act
            getSearchProfileService.getSearchProfileById(profileId);

            // Assert
            verify(searchProfileRepository).findById(profileId);
        }

        @Test
        @DisplayName("Should return profile with null optional fields")
        void shouldReturnProfileWithNullOptionalFields() {
            // Arrange - entity with null optional fields
            SearchProfileEntity entityWithNulls = SearchProfileEntity.builder()
                    .profileId(profileId)
                    .companyId(companyId)
                    .salaryMin(null)
                    .salaryMax(null)
                    .highestDegree(null)
                    .employmentType(null)
                    .country(null)
                    .build();

            SearchProfileResponseDto responseDtoWithNulls = SearchProfileResponseDto.builder()
                    .profileId(profileId)
                    .companyId(companyId)
                    .salaryMin(null)
                    .salaryMax(null)
                    .highestDegree(null)
                    .employmentTypes(Set.of())
                    .country(null)
                    .skillTagIds(Set.of())
                    .build();

            when(searchProfileRepository.findById(profileId)).thenReturn(Optional.of(entityWithNulls));
            when(searchProfileMapper.entityToResponseDto(entityWithNulls)).thenReturn(responseDtoWithNulls);

            // Act
            SearchProfileResponseDto result = getSearchProfileService.getSearchProfileById(profileId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getProfileId()).isEqualTo(profileId);
            assertThat(result.getSalaryMin()).isNull();
            assertThat(result.getSalaryMax()).isNull();
            assertThat(result.getHighestDegree()).isNull();
            assertThat(result.getCountry()).isNull();
        }
    }

    @Nested
    @DisplayName("Negative Tests - getSearchProfileById()")
    class GetSearchProfileByIdNegativeTests {

        @Test
        @DisplayName("Should throw EntityNotFoundException for null ID")
        void shouldThrowExceptionForNullId() {
            // Arrange
            when(searchProfileRepository.findById(null))
                    .thenThrow(new IllegalArgumentException("ID cannot be null"));

            // Act & Assert
            assertThatThrownBy(() -> getSearchProfileService.getSearchProfileById(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ID cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when repository throws unexpected error")
        void shouldThrowExceptionWhenRepositoryFails() {
            // Arrange
            when(searchProfileRepository.findById(profileId))
                    .thenThrow(new RuntimeException("Database connection lost"));

            // Act & Assert
            assertThatThrownBy(() -> getSearchProfileService.getSearchProfileById(profileId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Database connection lost");

            // Verify mapper was never called
            verify(searchProfileMapper, never()).entityToResponseDto(any());
        }

        @Test
        @DisplayName("Should throw exception when mapper fails during conversion")
        void shouldThrowExceptionWhenMapperFails() {
            // Arrange
            when(searchProfileRepository.findById(profileId)).thenReturn(Optional.of(entity));
            when(searchProfileMapper.entityToResponseDto(entity))
                    .thenThrow(new RuntimeException("Mapping error"));

            // Act & Assert
            assertThatThrownBy(() -> getSearchProfileService.getSearchProfileById(profileId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Mapping error");
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException with correct message format")
        void shouldThrowEntityNotFoundWithCorrectMessage() {
            // Arrange
            UUID specificId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
            when(searchProfileRepository.findById(specificId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> getSearchProfileService.getSearchProfileById(specificId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Search Profile not found with id: 123e4567-e89b-12d3-a456-426614174000");
        }

        @Test
        @DisplayName("Should not call mapper when entity not found")
        void shouldNotCallMapperWhenEntityNotFound() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();
            when(searchProfileRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> getSearchProfileService.getSearchProfileById(nonExistentId))
                    .isInstanceOf(EntityNotFoundException.class);

            // Verify mapper was never invoked
            verifyNoInteractions(searchProfileMapper);
        }
    }
}

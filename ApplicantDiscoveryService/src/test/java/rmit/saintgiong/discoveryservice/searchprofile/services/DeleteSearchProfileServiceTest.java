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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DeleteSearchProfileService.
 * Tests the business logic for deleting search profiles.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteSearchProfileService Tests")
class DeleteSearchProfileServiceTest {

    @Mock
    private SearchProfileRepository searchProfileRepository;

    @InjectMocks
    private DeleteSearchProfileService deleteSearchProfileService;

    private UUID profileId;

    @BeforeEach
    void setUp() {
        profileId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("deleteSearchProfile()")
    class DeleteSearchProfile {

        @Test
        @DisplayName("Should delete search profile successfully when it exists")
        void shouldDeleteSearchProfileSuccessfully() {
            // Arrange
            when(searchProfileRepository.existsById(profileId)).thenReturn(true);
            doNothing().when(searchProfileRepository).deleteById(profileId);

            // Act & Assert - no exception should be thrown
            assertThatCode(() -> deleteSearchProfileService.deleteSearchProfile(profileId))
                    .doesNotThrowAnyException();

            // Verify interactions
            verify(searchProfileRepository).existsById(profileId);
            verify(searchProfileRepository).deleteById(profileId);
        }

        @Test
        @DisplayName("Should verify existence before deleting")
        void shouldVerifyExistenceBeforeDeleting() {
            // Arrange
            when(searchProfileRepository.existsById(profileId)).thenReturn(true);
            doNothing().when(searchProfileRepository).deleteById(profileId);

            // Act
            deleteSearchProfileService.deleteSearchProfile(profileId);

            // Assert - verify order of operations
            var inOrder = inOrder(searchProfileRepository);
            inOrder.verify(searchProfileRepository).existsById(profileId);
            inOrder.verify(searchProfileRepository).deleteById(profileId);
        }

        @Test
        @DisplayName("Should delete profile with cascading skill tags")
        void shouldDeleteProfileWithCascadingSkillTags() {
            // Arrange - profile exists with skill tags (handled by JPA cascade)
            when(searchProfileRepository.existsById(profileId)).thenReturn(true);
            doNothing().when(searchProfileRepository).deleteById(profileId);

            // Act
            deleteSearchProfileService.deleteSearchProfile(profileId);

            // Assert
            verify(searchProfileRepository).deleteById(profileId);
        }
    }

    @Nested
    @DisplayName("Negative Tests - deleteSearchProfile()")
    class NegativeTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when profileId is null")
        void shouldThrowExceptionWhenProfileIdIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> deleteSearchProfileService.deleteSearchProfile(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Profile ID cannot be null");

            // Verify no repository interaction
            verifyNoInteractions(searchProfileRepository);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when profile does not exist")
        void shouldThrowExceptionWhenProfileNotFound() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();
            when(searchProfileRepository.existsById(nonExistentId)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> deleteSearchProfileService.deleteSearchProfile(nonExistentId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Search profile not found with ID: " + nonExistentId);

            // Verify
            verify(searchProfileRepository).existsById(nonExistentId);
            verify(searchProfileRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should throw exception when repository existsById fails")
        void shouldThrowExceptionWhenExistsByIdFails() {
            // Arrange
            when(searchProfileRepository.existsById(profileId))
                    .thenThrow(new RuntimeException("Database connection error"));

            // Act & Assert
            assertThatThrownBy(() -> deleteSearchProfileService.deleteSearchProfile(profileId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection error");

            // Verify
            verify(searchProfileRepository).existsById(profileId);
            verify(searchProfileRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should throw exception when repository deleteById fails")
        void shouldThrowExceptionWhenDeleteByIdFails() {
            // Arrange
            when(searchProfileRepository.existsById(profileId)).thenReturn(true);
            doThrow(new RuntimeException("Delete operation failed"))
                    .when(searchProfileRepository).deleteById(profileId);

            // Act & Assert
            assertThatThrownBy(() -> deleteSearchProfileService.deleteSearchProfile(profileId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Delete operation failed");

            // Verify
            verify(searchProfileRepository).existsById(profileId);
            verify(searchProfileRepository).deleteById(profileId);
        }

        @Test
        @DisplayName("Should not delete when existence check fails")
        void shouldNotDeleteWhenExistenceCheckFails() {
            // Arrange
            when(searchProfileRepository.existsById(profileId)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> deleteSearchProfileService.deleteSearchProfile(profileId))
                    .isInstanceOf(EntityNotFoundException.class);

            // Verify deleteById was never called
            verify(searchProfileRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle multiple delete calls for same profile")
        void shouldHandleMultipleDeleteCallsForSameProfile() {
            // First delete succeeds
            when(searchProfileRepository.existsById(profileId))
                    .thenReturn(true)  // First call
                    .thenReturn(false); // Second call
            doNothing().when(searchProfileRepository).deleteById(profileId);

            // First delete - should succeed
            assertThatCode(() -> deleteSearchProfileService.deleteSearchProfile(profileId))
                    .doesNotThrowAnyException();

            // Second delete - should fail (profile already deleted)
            assertThatThrownBy(() -> deleteSearchProfileService.deleteSearchProfile(profileId))
                    .isInstanceOf(EntityNotFoundException.class);

            // Verify
            verify(searchProfileRepository, times(2)).existsById(profileId);
            verify(searchProfileRepository, times(1)).deleteById(profileId);
        }

        @Test
        @DisplayName("Should work with different valid UUIDs")
        void shouldWorkWithDifferentValidUuids() {
            // Arrange - multiple different UUIDs
            UUID id1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
            UUID id2 = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
            UUID id3 = UUID.randomUUID();

            when(searchProfileRepository.existsById(any())).thenReturn(true);
            doNothing().when(searchProfileRepository).deleteById(any());

            // Act & Assert - all should succeed
            assertThatCode(() -> deleteSearchProfileService.deleteSearchProfile(id1))
                    .doesNotThrowAnyException();
            assertThatCode(() -> deleteSearchProfileService.deleteSearchProfile(id2))
                    .doesNotThrowAnyException();
            assertThatCode(() -> deleteSearchProfileService.deleteSearchProfile(id3))
                    .doesNotThrowAnyException();

            // Verify all three were processed
            verify(searchProfileRepository, times(3)).existsById(any());
            verify(searchProfileRepository, times(3)).deleteById(any());
        }
    }
}

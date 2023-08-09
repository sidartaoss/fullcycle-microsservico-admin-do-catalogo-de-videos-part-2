package com.fullcycle.admin.catalogo.application.category.deactivate;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DeactivateCategoryUseCaseTest extends UseCaseTest {

    /**
     * 1. Teste do caminho feliz
     * 2. Teste passando uma propriedade inválida (name)
     * 4. Teste simulando um erro genérico vindo do Gateway
     * 4. Teste ativar categoria passando ID inválido.
     */

    @Mock
    CategoryGateway categoryGateway;

    @InjectMocks
    DefaultDeactivateCategoryUseCase deactivateCategoryUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Nested
    @DisplayName("Deactivate with valid command")
    class DeactivateWithValidCommand {

        @BeforeEach
        void init() {
            when(categoryGateway.update(any(Category.class)))
                    .thenAnswer(returnsFirstArg());
        }

        @Test
        void Given_a_valid_command_When_calls_deactivate_category_Then_should_return_an_ouput() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            final var actualOutput = deactivateCategoryUseCase.execute(expectedId.getValue()).get();
            // then
            assertNotNull(actualOutput);
        }

        @Test
        void Given_a_valid_command_When_calls_deactivate_category_Then_should_return_an_ouput_with_category_id() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            final var actualOutput = deactivateCategoryUseCase.execute(expectedId.getValue()).get();
            // then
            assertNotNull(actualOutput.id());
        }

        @Test
        void Given_a_valid_command_When_calls_deactivate_Then_should_verify_that_gateway_findById_was_invoked_once() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway, times(1)).findById(any(CategoryID.class));
        }

        @Test
        void Given_a_valid_command_When_calls_deactivate_Then_should_verify_gateway_update_was_invoked_only_once() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway, times(1)).update(any(Category.class));
        }

        @Test
        void Given_a_valid_command_When_calls_deactivate_Then_should_verify_id_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedId, anUpdatedCategory.getId())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_deactivate_Then_should_verify_name_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedName, anUpdatedCategory.getName())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_deactivate_Then_should_verify_description_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_deactivate_Then_should_verify_isActive_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            final var expectedIsActive = ActivationStatus.INACTIVE;
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedIsActive, anUpdatedCategory.getActivationStatus())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_deactivate_Then_should_verify_createdAt_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_deactivate_Then_should_verify_updatedAt_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(Category.with(aCategory)));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    anUpdatedCategory.getUpdatedAt().isAfter(aCategory.getUpdatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_deactivate_Then_should_verify_deletedAt_is_null() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.nonNull(anUpdatedCategory.getDeletedAt())
            ));
        }
    }

    @Nested
    @DisplayName("Deactivate with generic error from gateway")
    class DeactivateWithGenericErrorFromGateway {

        private static final String GATEWAY_ERROR = "Gateway Error";

        @BeforeEach
        void init() {
            when(categoryGateway.update(any(Category.class)))
                    .thenThrow(new IllegalStateException(GATEWAY_ERROR));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_random_exception_Then_should_return_an_error_message() {
            // given
            final String expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            final var notification = deactivateCategoryUseCase.execute(expectedId.getValue()).getLeft();
            // then
            assertEquals(GATEWAY_ERROR, notification.firstError().message());
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_gateway_update_was_invoked_once() {
            // given
            final String expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway, times(1)).update(any(Category.class));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_id_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedId, anUpdatedCategory.getId())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_name_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedName, anUpdatedCategory.getName())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_description_was_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_isActive_was_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            final var expectedIsActive = ActivationStatus.INACTIVE;
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedIsActive, anUpdatedCategory.getActivationStatus())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_createdAt_was_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_updatedAt_was_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(Category.with(aCategory)));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    anUpdatedCategory.getUpdatedAt().isAfter(aCategory.getUpdatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_deletedAt_is_null() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            deactivateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.nonNull(anUpdatedCategory.getDeletedAt())
            ));
        }
    }

    @Nested
    @DisplayName("Activate with invalid id")
    class ActivateWithInvalidId {

        @Test
        void Given_a_command_with_invalid_id_When_calls_deactivate_category_Then_should_return_not_found_exception() {
            // given
            final var expectedId = "123";
            when(categoryGateway.findById(CategoryID.from(expectedId)))
                    .thenReturn(Optional.empty());
            final Executable invokeInvalidMethod = () -> deactivateCategoryUseCase.execute(expectedId);
            final var expectedErrorMessage = "Category with ID %s was not found"
                    .formatted(expectedId);
            // when
            final var actualException = assertThrows(DomainException.class, invokeInvalidMethod);
            // then
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        }

        @Test
        void Given_a_command_with_invalid_id_When_calls_deactivate_category_Then_should_return_error_count_as_1() {
            // given
            final var expectedId = "123";
            when(categoryGateway.findById(CategoryID.from(expectedId)))
                    .thenReturn(Optional.empty());
            final Executable invokeInvalidMethod = () -> deactivateCategoryUseCase.execute(expectedId);
            final var expectedErrorCount = 1;
            // when
            final var actualException = assertThrows(DomainException.class, invokeInvalidMethod);
            // then
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }

        @Test
        void Given_a_command_with_invalid_id_When_calls_deactivate_Then_should_verify_that_findById_was_invoked_once() {
            // given
            final var expectedId = "123";
            when(categoryGateway.findById(CategoryID.from(expectedId)))
                    .thenReturn(Optional.empty());
            final Executable invokeInvalidMethod = () -> deactivateCategoryUseCase.execute(expectedId);
            // when
            assertThrows(DomainException.class, invokeInvalidMethod);
            // then
            verify(categoryGateway, times(1)).findById(CategoryID.from(expectedId));
        }

        @Test
        void Given_a_command_with_invalid_id_When_calls_deactivate_Then_should_verify_gateway_update_was_never_invoked() {
            // given
            final var expectedId = "123";
            when(categoryGateway.findById(CategoryID.from(expectedId)))
                    .thenReturn(Optional.empty());
            final Executable invokeInvalidMethod = () -> deactivateCategoryUseCase.execute(expectedId);
            // when
            assertThrows(DomainException.class, invokeInvalidMethod);
            // then
            verify(categoryGateway, never()).update(any(Category.class));
        }
    }
}

package com.fullcycle.admin.catalogo.application.category.activate;

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
class ActivateCategoryUseCaseTest extends UseCaseTest {

    /**
     * 1. Teste do caminho feliz
     * 2. Teste passando uma propriedade inválida (name)
     * 4. Teste simulando um erro genérico vindo do Gateway
     * 4. Teste ativar categoria passando ID inválido.
     */

    @Mock
    CategoryGateway categoryGateway;

    @InjectMocks
    DefaultActivateCategoryUseCase activateCategoryUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Nested
    @DisplayName("Activate a category with valid category id")
    class ActivateWithValidId {

        @BeforeEach
        void init() {
            when(categoryGateway.update(any(Category.class)))
                    .thenAnswer(returnsFirstArg());
        }

        @Test
        void Given_a_valid_command_When_calls_activate_category_Then_should_return_an_ouput() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            final var actualOutput = activateCategoryUseCase.execute(expectedId.getValue()).get();
            // then
            assertNotNull(actualOutput);
        }

        @Test
        void Given_a_valid_command_When_calls_activate_category_Then_should_return_an_ouput_and_category_id() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            final var actualOutput = activateCategoryUseCase.execute(expectedId.getValue()).get();
            // then
            assertNotNull(actualOutput.id());
        }

        @Test
        void Given_a_valid_command_When_calls_activate_Then_should_verify_gateway_findById_was_invoked_only_once() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway, times(1)).findById(any(CategoryID.class));
        }

        @Test
        void Given_a_valid_command_When_calls_activate_Then_should_verify_gateway_update_was_invoked_only_once() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway, times(1)).update(any(Category.class));
        }

        @Test
        void Given_a_valid_command_When_calls_activate_Then_should_verify_category_id_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedId, anUpdatedCategory.getId())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_activate_Then_should_verify_category_name_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedName, anUpdatedCategory.getName())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_activate_Then_should_verify_description_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_activate_Then_should_verify_activation_status_was_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            final var expectedIsActive = ActivationStatus.ACTIVE;
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedIsActive, anUpdatedCategory.getActivationStatus())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_activate_Then_should_verify_createdAt_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_activate_Then_should_verify_updatedAt_was_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(Category.with(aCategory)));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    anUpdatedCategory.getUpdatedAt().isAfter(aCategory.getUpdatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_activate_Then_should_verify_category_deletedAt_is_null() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.isNull(anUpdatedCategory.getDeletedAt())
            ));
        }
    }

    @Nested
    @DisplayName("Activate a category with generic error from gateway")
    class ActivateWithGenericErrorFromGateway {

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
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            final var notification = activateCategoryUseCase.execute(expectedId.getValue()).getLeft();
            // then
            assertEquals(GATEWAY_ERROR, notification.firstError().message());
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_gateway_update_is_invoked_once() {
            // given
            final String expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway, times(1)).update(any(Category.class));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_id_is_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedId, anUpdatedCategory.getId())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_name_is_passed_to_gateway_update() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedName, anUpdatedCategory.getName())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_description_is_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_isActive_is_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            final var expectedIsActive = ActivationStatus.ACTIVE;
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(expectedIsActive, anUpdatedCategory.getActivationStatus())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_createdAt_is_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_updatedAt_is_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(Category.with(aCategory)));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
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
            final var aCategory = Category.newCategory(expectedName, expectedDescription)
                    .deactivate();
            final var expectedId = aCategory.getId();
            when(categoryGateway.findById(expectedId))
                    .thenReturn(Optional.of(aCategory));
            // when
            activateCategoryUseCase.execute(expectedId.getValue());
            // then
            verify(categoryGateway).update(argThat(anUpdatedCategory ->
                    Objects.isNull(anUpdatedCategory.getDeletedAt())
            ));
        }
    }

    @Nested
    @DisplayName("Activate a category with invalid category id")
    class ActivateWithInvalidId {

        @Test
        void Given_a_command_with_invalid_id_When_calls_activate_category_Then_should_return_not_found_exception() {
            // given
            final var expectedId = "123";
            when(categoryGateway.findById(CategoryID.from(expectedId)))
                    .thenReturn(Optional.empty());
            final Executable invokeInvalidMethod = () -> activateCategoryUseCase.execute(expectedId);
            final var expectedErrorMessage = "Category with ID %s was not found"
                    .formatted(expectedId);
            // when
            final var actualException = assertThrows(DomainException.class, invokeInvalidMethod);
            // then
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        }

        @Test
        void Given_a_command_with_invalid_id_When_calls_activate_category_Then_should_return_error_count_as_1() {
            // given
            final var expectedId = "123";
            when(categoryGateway.findById(CategoryID.from(expectedId)))
                    .thenReturn(Optional.empty());
            final Executable invokeInvalidMethod = () -> activateCategoryUseCase.execute(expectedId);
            final var expectedErrorCount = 1;
            // when
            final var actualException = assertThrows(DomainException.class, invokeInvalidMethod);
            // then
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }

        @Test
        void Given_a_command_with_invalid_id_When_calls_activate_Then_should_verify_findById_was_invoked_only_once() {
            // given
            final var expectedId = "123";
            when(categoryGateway.findById(CategoryID.from(expectedId)))
                    .thenReturn(Optional.empty());
            final Executable invokeInvalidMethod = () -> activateCategoryUseCase.execute(expectedId);
            // when
            assertThrows(DomainException.class, invokeInvalidMethod);
            // then
            verify(categoryGateway, times(1)).findById(CategoryID.from(expectedId));
        }

        @Test
        void Given_a_command_with_invalid_id_When_calls_activate_Then_should_verify_gateway_update_was_never_invoked() {
            // given
            final var expectedId = "123";
            when(categoryGateway.findById(CategoryID.from(expectedId)))
                    .thenReturn(Optional.empty());
            final Executable invokeInvalidMethod = () -> activateCategoryUseCase.execute(expectedId);
            // when
            assertThrows(DomainException.class, invokeInvalidMethod);
            // then
            verify(categoryGateway, never()).update(any(Category.class));
        }
    }
}

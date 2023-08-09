package com.fullcycle.admin.catalogo.application.category.create;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CreateCategoryUseCaseTest extends UseCaseTest {

    /**
     * 1. Teste do caminho feliz
     * 2. Teste passando uma propriedade inválida (name)
     * 3. Teste criando uma categoria inativa
     * 4. Teste simulando um erro genérico vindo do Gateway
     */
    @Mock
    CategoryGateway categoryGateway;

    @InjectMocks
    DefaultCreateCategoryUseCase createCategoryUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Nested
    @DisplayName("Create with valid command")
    class CreateWithValidCommand {

        @BeforeEach
        void init() {
            when(categoryGateway.create(any(Category.class)))
                    .thenAnswer(returnsFirstArg());
        }

        @Test
        void Given_a_valid_command_When_calls_create_category_Then_should_return_an_ouput() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            final var actualOutput = createCategoryUseCase.execute(aCommand).get();
            // then
            assertNotNull(actualOutput);
        }

        @Test
        void Given_a_valid_command_When_calls_create_Then_should_return_an_ouput_and_a_newly_created_category_id() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            final var actualOutput = createCategoryUseCase.execute(aCommand).get();
            // then
            assertNotNull(actualOutput.id());
        }

        @Test
        void Given_a_valid_command_When_calls_create_category_Then_should_verify_gateway_create_is_invoked_only_once() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway, times(1)).create(any(Category.class));
        }

        @Test
        void Given_a_valid_command_When_calls_create_Then_should_verify_id_was_passed_to_gateway_create() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.nonNull(aCategory.getId())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_create_Then_should_verify_name_was_passed_to_gateway_create() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.equals(expectedName, aCategory.getName())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_create_Then_should_verify_description_was_passed_to_gateway_create() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.equals(expectedDescription, aCategory.getDescription())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_create_Then_should_verify_activation_status_is_passed_to_gateway_create() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            final var expectedIsActive = ActivationStatus.ACTIVE;
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.equals(expectedIsActive, aCategory.getActivationStatus())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_create_Then_should_verify_createdAt_was_passed_to_gateway_create() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.nonNull(aCategory.getCreatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_create_Then_should_verify_updatedAt_was_passed_to_gateway_create() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.nonNull(aCategory.getUpdatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_create_Then_should_verify_argument_category_deletedAt_was_null() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.isNull(aCategory.getDeletedAt())
            ));
        }
    }

    @Nested
    @DisplayName("Create with invalid name")
    class CreateWithInvalidName {

        @Test
        void Given_an_invalid_name_When_calls_create_category_Then_should_return_an_error_message() {
            // given
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(null,
                    expectedDescription);
            final var expectedErrorMessage = "'name' should not be null";
            // when
            Notification notification = createCategoryUseCase.execute(aCommand).getLeft();
            // then
            assertEquals(expectedErrorMessage, notification.firstError().message());
        }

        @Test
        void Given_an_invalid_name_When_calls_create_category_Then_should_return_error_count_as_1() {
            // given
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(null,
                    expectedDescription);
            final var expectedErrorCount = 1;
            // when
            Notification notification = createCategoryUseCase.execute(aCommand).getLeft();
            // then
            assertEquals(expectedErrorCount, notification.getErrors().size());
        }

        @Test
        void Given_an_invalid_name_When_calls_create_category_Then_should_verify_that_create_has_not_been_called() {
            // given
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(null,
                    expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway, times(0)).create(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Create with generic error from gateway")
    class CreateWithGenericErrorFromGateway {

        private static final String GATEWAY_ERROR = "Gateway Error";

        @BeforeEach
        void init() {
            when(categoryGateway.create(any(Category.class)))
                    .thenThrow(new IllegalStateException(GATEWAY_ERROR));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_random_exception_Then_should_return_an_error_message() {
            // given
            final String expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            Notification notification = createCategoryUseCase.execute(aCommand).getLeft();
            // then
            assertEquals(GATEWAY_ERROR, notification.firstError().message());
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_gateway_create_was_invoked_once() {
            // given
            final String expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway, times(1)).create(any(Category.class));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_id_was_passed_to_gateway_create() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.nonNull(aCategory.getId())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_name_was_passed_to_gateway_create() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.equals(expectedName, aCategory.getName())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_description_is_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.equals(expectedDescription, aCategory.getDescription())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_isActive_is_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            final var expectedIsActive = ActivationStatus.ACTIVE;
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.equals(expectedIsActive, aCategory.getActivationStatus())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_createdAt_is_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.nonNull(aCategory.getCreatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_updatedAt_was_passed_to_gateway() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.nonNull(aCategory.getUpdatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_When_gateway_throws_exception_Then_should_verify_deletedAt_was_null() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription);
            // when
            createCategoryUseCase.execute(aCommand);
            // then
            verify(categoryGateway).create(argThat(aCategory ->
                    Objects.isNull(aCategory.getDeletedAt())
            ));
        }
    }
}

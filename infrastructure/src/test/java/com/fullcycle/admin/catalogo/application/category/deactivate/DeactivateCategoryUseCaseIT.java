package com.fullcycle.admin.catalogo.application.category.deactivate;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@IntegrationTest
public class DeactivateCategoryUseCaseIT {

    @Autowired
    private DeactivateCategoryUseCase deactivateCategoryUseCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("Deactivate with a valid command")
    class DeactivateWithValidCommand {

        @Test
        void Given_a_valid_command_When_calls_deactivate_category_Then_should_return_an_inactive_category_id() {
            // given
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var aCategory = Category.newCategory(expectedName, expectedDescription);
            final var expectedId = aCategory.getId();
            final var expectedIsActive = ActivationStatus.INACTIVE;

            assertEquals(0, categoryRepository.count());

            categoryRepository.save(CategoryJpaEntity.from(aCategory));

            assertEquals(1, categoryRepository.count());

            // when
            final var actualOutput = deactivateCategoryUseCase.execute(expectedId.getValue())
                    .get();

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            categoryRepository.findById(actualOutput.id())
                    .ifPresent(actualCategory -> {

                        assertEquals(expectedName, actualCategory.getName());
                        assertEquals(expectedDescription, actualCategory.getDescription());
                        assertEquals(expectedIsActive, actualCategory.getActivationStatus());
                        assertNotNull(actualCategory.getCreatedAt());
                        assertNotNull(actualCategory.getUpdatedAt());
                        assertNotNull(actualCategory.getDeletedAt());
                    });
        }
    }
}

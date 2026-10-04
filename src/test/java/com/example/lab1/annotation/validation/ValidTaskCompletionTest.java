package com.example.lab1.annotation.validation;

import com.example.lab1.dto.TaskDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidTaskCompletionTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory();

        validator = factory.getValidator();
    }

    @Test
    @DisplayName("AC2: valid completed task has no violations")
    void validCompletedTask_hasNoViolations() {
        // Arrange
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Finish lab");
        taskDto.setDescription("Laboratory work is completed");
        taskDto.setStatus("DONE");
        taskDto.setPriority("HIGH");

        // Act
        Set<ConstraintViolation<TaskDto>> violations =
                validator.validate(taskDto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("AC3: completed task without description produces violation")
    void completedTaskWithoutDescription_producesViolation() {
        // Arrange
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Finish lab");
        taskDto.setDescription("");
        taskDto.setStatus("DONE");
        taskDto.setPriority("HIGH");

        // Act
        Set<ConstraintViolation<TaskDto>> violations =
                validator.validate(taskDto);

        // Assert
        assertEquals(1, violations.size());

        ConstraintViolation<TaskDto> violation =
                violations.iterator().next();

        assertEquals(
                "Task with DONE status must have a description",
                violation.getMessage()
        );
    }
}
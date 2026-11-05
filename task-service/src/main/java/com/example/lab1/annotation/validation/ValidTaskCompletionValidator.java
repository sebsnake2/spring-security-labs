package com.example.lab1.annotation.validation;

import com.example.lab1.dto.TaskDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidTaskCompletionValidator
        implements ConstraintValidator<ValidTaskCompletion, TaskDto> {

    @Override
    public boolean isValid(
            TaskDto taskDto,
            ConstraintValidatorContext context
    ) {
        if (taskDto == null) {
            return true;
        }

        if (!"DONE".equalsIgnoreCase(taskDto.getStatus())) {
            return true;
        }

        return taskDto.getDescription() != null
                && !taskDto.getDescription().isBlank();
    }
}
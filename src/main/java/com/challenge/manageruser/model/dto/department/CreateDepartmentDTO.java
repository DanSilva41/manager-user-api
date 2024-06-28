package com.challenge.manageruser.model.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record CreateDepartmentDTO(
        @NotBlank(message = "Cannot be empty")
        @Size(min = 3, max = 60, message = "Must have between {min} and {max} characters")
        String name,

        @NotBlank(message = "Cannot be empty")
        @Size(min = 5, max = 100, message = "Must have between {min} and {max} characters")
        String description
) {

    @Override
    public String name() {
        if (Objects.nonNull(name) && !name.isEmpty())
            return name.toUpperCase().trim();
        return name;
    }
}

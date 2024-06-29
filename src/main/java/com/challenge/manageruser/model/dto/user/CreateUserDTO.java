package com.challenge.manageruser.model.dto.user;

import com.challenge.manageruser.model.dto.person.CreatePersonDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record CreateUserDTO(
        @NotBlank(message = "Cannot be empty")
        @Size(min = 5, max = 20, message = "Must have between {min} and {max} characters")
        String username,

        @NotBlank(message = "Cannot be empty")
        @Size(min = 6, max = 60, message = "Must have between {min} and {max} characters")
        String password,

        @NotNull(message = "Cannot be null")
        @Positive
        Integer departmentCode,

        @Valid
        @NotNull(message = "Cannot be null")
        CreatePersonDTO person
) {

    @Override
    public String username() {
        if (Objects.nonNull(username) && !username.isEmpty())
            return username.toLowerCase().trim();
        return username;
    }
}

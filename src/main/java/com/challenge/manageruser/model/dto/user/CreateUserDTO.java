package com.challenge.manageruser.model.dto.user;

import com.challenge.manageruser.model.dto.person.CreatePersonDTO;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@JsonNaming(SnakeCaseStrategy.class)
public record CreateUserDTO(
        @NotBlank(message = "Cannot be empty")
        @Size(min = 5, max = 20, message = "Must have between {min} and {max} characters")
        String username,

        @NotBlank(message = "Cannot be empty")
        @Size(min = 6, max = 60, message = "Must have between {min} and {max} characters")
        String password,

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

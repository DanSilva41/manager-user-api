package com.challenge.manageruser.model.dto.user;

import com.challenge.manageruser.model.dto.person.CreatePersonDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record CreateUserDTO(
        @NotBlank(message = "Username can not be empty")
        @Size(min = 5, max = 20)
        String username,

        @NotBlank(message = "Password can not be empty")
        @Size(min = 6, max = 60)
        String password,

        @Valid
        @NotNull(message = "Personal data can not be empty")
        CreatePersonDTO person
) {

    @Override
    public String username() {
        if (Objects.nonNull(username) && !username.isEmpty())
            return username.toLowerCase().trim();
        return username;
    }
}

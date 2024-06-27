package com.challenge.manageruser.model.dto.person;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.text.WordUtils;

import java.util.Objects;

@JsonNaming(SnakeCaseStrategy.class)
public record CreatePersonDTO(
        @NotBlank(message = "Cannot be empty")
        @Size(min = 2, max = 60, message = "Must have between {min} and {max} characters")
        String firstName,

        @NotBlank(message = "Cannot be empty")
        @Size(min = 2, max = 60, message = "Must have between {min} and {max} characters")
        String lastName,

        @Email
        @NotBlank(message = "Cannot be empty")
        @Size(min = 5, max = 100, message = "Must have between {min} and {max} characters")
        String email
) {

    @Override
    public String firstName() {
        if (Objects.nonNull(firstName) && !firstName.isEmpty())
            return WordUtils.capitalize(firstName.toLowerCase().trim());
        return firstName;
    }

    @Override
    public String lastName() {
        if (Objects.nonNull(lastName) && !lastName.isEmpty())
            return WordUtils.capitalize(lastName.toLowerCase().trim());
        return lastName;
    }

    @Override
    public String email() {
        if (Objects.nonNull(email) && !email.isEmpty())
            return email.toLowerCase().trim();
        return email;
    }
}

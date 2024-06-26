package com.challenge.manageruser.model.dto.person;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.text.WordUtils;

import java.util.Objects;

public record CreatePersonDTO(
        @NotBlank(message = "First name can not be empty")
        @Size(min = 2, max = 60)
        String firstName,

        @NotBlank(message = "Last name can not be empty")
        @Size(min = 2, max = 60)
        String lastName,

        @Email
        @NotBlank(message = "Email can not be empty")
        @Size(min = 5, max = 100)
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

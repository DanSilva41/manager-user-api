package com.challenge.manageruser.model.dto.person;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.text.WordUtils;

import java.util.Objects;

public class CreatePersonDTO {

    @NotBlank(message = "First name can not be empty")
    @Size(min = 2, max = 60)
    private String firstName;

    @NotBlank(message = "Last name can not be empty")
    @Size(min = 2, max = 60)
    private String lastName;

    @Email
    @NotBlank(message = "Email can not be empty")
    @Size(min = 5, max = 100)
    private String email;

    public CreatePersonDTO() {
        // default constructor
    }

    public CreatePersonDTO(final String firstName, final String lastName, final String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        if (Objects.nonNull(firstName) && !firstName.isEmpty())
            firstName = WordUtils.capitalize(firstName.toLowerCase().trim());
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        if (Objects.nonNull(lastName) && !lastName.isEmpty())
            lastName = WordUtils.capitalize(lastName.toLowerCase().trim());
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        if (Objects.nonNull(email) && !email.isEmpty())
            email = email.toLowerCase().trim();
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CreatePersonDTO createPerson = (CreatePersonDTO) o;
        return Objects.equals(email, createPerson.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}

package com.challenge.manageruser.model.dto.user;

import com.challenge.manageruser.model.dto.person.CreatePersonDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class CreateUserDTO {

    @NotBlank(message = "Username can not be empty")
    @Size(min = 5, max = 20)
    private String username;

    @NotBlank(message = "Password can not be empty")
    @Size(min = 6, max = 60)
    private String password;

    @Valid
    @NotNull(message = "Personal data can not be empty")
    private CreatePersonDTO person;

    public CreateUserDTO() {
        // default constructor
    }

    public CreateUserDTO(final String username, final CreatePersonDTO person) {
        this.username = username;
        this.person = person;
    }

    public String getUsername() {
        if (Objects.nonNull(username) && !username.isEmpty())
            username = username.toLowerCase().trim();
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public CreatePersonDTO getPerson() {
        return person;
    }

    public void setPerson(final CreatePersonDTO person) {
        this.person = person;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CreateUserDTO createUser = (CreateUserDTO) o;
        return Objects.equals(username, createUser.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}

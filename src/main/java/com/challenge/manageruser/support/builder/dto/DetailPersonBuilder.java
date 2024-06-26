package com.challenge.manageruser.support.builder.dto;

import com.challenge.manageruser.model.dto.person.DetailPersonDTO;

import java.time.LocalDateTime;

public class DetailPersonBuilder {

    private Integer code;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DetailPersonDTO build() {
        return new DetailPersonDTO(code, firstName, lastName, email, createdAt, updatedAt);
    }

    public DetailPersonBuilder code(final Integer code) {
        this.code = code;
        return this;
    }

    public DetailPersonBuilder firstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }

    public DetailPersonBuilder lastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }

    public DetailPersonBuilder email(final String email) {
        this.email = email;
        return this;
    }

    public DetailPersonBuilder createdAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public DetailPersonBuilder updatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}

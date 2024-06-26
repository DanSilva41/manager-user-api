package com.challenge.manageruser.support.builder.dto;

import com.challenge.manageruser.model.dto.person.DetailPersonDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;

import java.time.LocalDateTime;

public class DetailUserBuilder {

    private Integer code;
    private String username;
    private boolean active;

    private DetailPersonDTO person;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DetailUserDTO build() {
        return new DetailUserDTO(code, username, active, person, createdAt, updatedAt);
    }

    public DetailUserBuilder code(final Integer code) {
        this.code = code;
        return this;
    }

    public DetailUserBuilder username(final String username) {
        this.username = username;
        return this;
    }

    public DetailUserBuilder active(final boolean active) {
        this.active = active;
        return this;
    }

    public DetailUserBuilder person(final DetailPersonDTO person) {
        this.person = person;
        return this;
    }

    public DetailUserBuilder createdAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public DetailUserBuilder updatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}

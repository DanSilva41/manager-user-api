package com.challenge.manageruser.support.builder.dto;

import com.challenge.manageruser.model.dto.user.SimpleUserDTO;

import java.time.LocalDateTime;

public class SimpleUserBuilder {

    private Integer code;
    private String username;
    private Boolean active;
    private String fullName;
    private String departmentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SimpleUserDTO build() {
        return new SimpleUserDTO(code, username, active, fullName, departmentName, createdAt, updatedAt);
    }

    public SimpleUserBuilder code(final Integer code) {
        this.code = code;
        return this;
    }

    public SimpleUserBuilder username(final String username) {
        this.username = username;
        return this;
    }

    public SimpleUserBuilder active(final Boolean active) {
        this.active = active;
        return this;
    }

    public SimpleUserBuilder fullName(final String fullName) {
        this.fullName = fullName;
        return this;
    }

    public SimpleUserBuilder departmentName(final String departmentName) {
        this.departmentName = departmentName;
        return this;
    }

    public SimpleUserBuilder createdAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public SimpleUserBuilder updatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}

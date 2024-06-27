package com.challenge.manageruser.support.builder.dto;

import com.challenge.manageruser.model.dto.department.DetailDepartmentDTO;

import java.time.LocalDateTime;

public class DetailDepartmentBuilder {

    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DetailDepartmentDTO build() {
        return new DetailDepartmentDTO(name, description, createdAt, updatedAt);
    }

    public DetailDepartmentBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public DetailDepartmentBuilder description(final String description) {
        this.description = description;
        return this;
    }

    public DetailDepartmentBuilder createdAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public DetailDepartmentBuilder updatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}

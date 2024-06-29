package com.challenge.manageruser.model.dto.department;

import com.challenge.manageruser.support.builder.dto.DetailDepartmentBuilder;

import java.time.LocalDateTime;

public record DetailDepartmentDTO(
        Integer code,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DetailDepartmentBuilder builder() {
        return new DetailDepartmentBuilder();
    }
}

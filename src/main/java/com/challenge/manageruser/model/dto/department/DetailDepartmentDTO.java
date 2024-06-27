package com.challenge.manageruser.model.dto.department;

import com.challenge.manageruser.support.builder.dto.DetailDepartmentBuilder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record DetailDepartmentDTO(
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DetailDepartmentBuilder builder() {
        return new DetailDepartmentBuilder();
    }
}

package com.challenge.manageruser.model.dto.person;

import com.challenge.manageruser.support.builder.dto.DetailPersonBuilder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record DetailPersonDTO(
        Integer code,
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DetailPersonBuilder builder() {
        return new DetailPersonBuilder();
    }
}

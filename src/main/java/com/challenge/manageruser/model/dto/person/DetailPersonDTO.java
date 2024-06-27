package com.challenge.manageruser.model.dto.person;

import com.challenge.manageruser.support.builder.dto.DetailPersonBuilder;

import java.time.LocalDateTime;

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

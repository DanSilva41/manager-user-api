package com.challenge.manageruser.model.dto.user;

import com.challenge.manageruser.model.dto.person.DetailPersonDTO;
import com.challenge.manageruser.support.builder.dto.DetailUserBuilder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record DetailUserDTO(
        Integer code,
        String username,
        Boolean active,
        DetailPersonDTO person,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DetailUserBuilder builder() {
        return new DetailUserBuilder();
    }
}

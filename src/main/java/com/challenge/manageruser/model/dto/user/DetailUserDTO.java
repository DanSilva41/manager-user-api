package com.challenge.manageruser.model.dto.user;

import com.challenge.manageruser.model.dto.person.DetailPersonDTO;
import com.challenge.manageruser.support.builder.dto.DetailUserBuilder;

import java.time.LocalDateTime;

public record DetailUserDTO(
        Integer code,
        String username,
        boolean active,
        DetailPersonDTO person,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DetailUserBuilder builder() {
        return new DetailUserBuilder();
    }
}

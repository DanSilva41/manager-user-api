package com.challenge.manageruser.model.dto.user;

import com.challenge.manageruser.support.builder.dto.SimpleUserBuilder;

import java.time.LocalDateTime;

public record SimpleUserDTO(
        Integer code,
        String username,
        Boolean active,
        String fullName,
        String departmentName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static SimpleUserBuilder builder() {
        return new SimpleUserBuilder();
    }
}

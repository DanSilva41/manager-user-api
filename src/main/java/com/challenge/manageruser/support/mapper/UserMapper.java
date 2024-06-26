package com.challenge.manageruser.support.mapper;

import com.challenge.manageruser.model.dto.person.DetailPersonDTO;
import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.entity.backing.Person;
import com.challenge.manageruser.model.entity.security.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class UserMapper {

    private UserMapper() throws IllegalAccessException {
        throw new IllegalAccessException("Do not instantiate this class, use statically");
    }

    public static User toUser(final CreateUserDTO newUser) {
        return User.builder()
                .username(newUser.username())
                .password(newUser.password())
                .person(Person.builder()
                        .firstName(newUser.person().firstName())
                        .lastName(newUser.person().lastName())
                        .email(newUser.person().email())
                        .build())
                .active(true)
                .build();
    }

    public static DetailUserDTO toDetailUser(final User user) {
        return DetailUserDTO.builder()
                .code(user.getCode())
                .username(user.getUsername())
                .active(user.isActive())
                .person(DetailPersonDTO.builder()
                        .code(user.getPerson().getCode())
                        .firstName(user.getPerson().getFirstName())
                        .lastName(user.getPerson().getLastName())
                        .email(user.getPerson().getEmail())
                        .createdAt(LocalDateTime.ofInstant(user.getPerson().getCreatedAt(), ZoneOffset.UTC))
                        .updatedAt(LocalDateTime.ofInstant(user.getPerson().getUpdatedAt(), ZoneOffset.UTC))
                        .build())
                .createdAt(LocalDateTime.ofInstant(user.getCreatedAt(), ZoneOffset.UTC))
                .updatedAt(LocalDateTime.ofInstant(user.getUpdatedAt(), ZoneOffset.UTC))
                .build();
    }
}

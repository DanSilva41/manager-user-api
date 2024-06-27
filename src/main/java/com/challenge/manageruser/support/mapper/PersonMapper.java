package com.challenge.manageruser.support.mapper;

import com.challenge.manageruser.model.dto.person.CreatePersonDTO;
import com.challenge.manageruser.model.dto.person.DetailPersonDTO;
import com.challenge.manageruser.model.entity.backing.Person;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class PersonMapper {

    private PersonMapper() throws IllegalAccessException {
        throw new IllegalAccessException("Do not instantiate this class, use statically");
    }

    public static Person toPerson(final CreatePersonDTO newPerson) {
        return Person.builder()
                .firstName(newPerson.firstName())
                .lastName(newPerson.lastName())
                .email(newPerson.email())
                .build();
    }

    public static DetailPersonDTO toDetailPerson(final Person person) {
        return DetailPersonDTO.builder()
                .code(person.getCode())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .email(person.getEmail())
                .createdAt(LocalDateTime.ofInstant(person.getCreatedAt(), ZoneOffset.UTC))
                .updatedAt(LocalDateTime.ofInstant(person.getUpdatedAt(), ZoneOffset.UTC))
                .build();
    }
}

package com.challenge.manageruser.support.mapper;

import com.challenge.manageruser.model.dto.person.CreatePersonDTO;
import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.entity.backing.Person;
import com.challenge.manageruser.model.entity.security.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.challenge.manageruser.factory.FakerFactory.faker;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    @Test
    void shouldMapperToUserFromCreateUserDTO() {
        final CreatePersonDTO newPerson = new CreatePersonDTO(
                faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress()
        );
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), newPerson);

        final User mappedUser = UserMapper.toUser(newUser);
        assertNotNull(mappedUser);
        assertEquals(newUser.username(), mappedUser.getUsername());
        assertEquals(newUser.password(), mappedUser.getPassword());
        assertNotNull(mappedUser.getPerson());
        assertEquals(newUser.person().firstName(), mappedUser.getPerson().getFirstName());
        assertEquals(newUser.person().lastName(), mappedUser.getPerson().getLastName());
        assertEquals(newUser.person().email(), mappedUser.getPerson().getEmail());
    }

    @Test
    void shouldMapperToDetailUserFromUser() {
        final User user = User.builder()
                .username(faker.internet().username())
                .password(faker.internet().password())
                .active(true)
                .person(Person.builder()
                        .firstName(faker.name().firstName())
                        .lastName(faker.name().lastName())
                        .email(faker.internet().emailAddress())
                        .build())
                .build();
        ReflectionTestUtils.setField(user, "createdAt", Instant.now());
        ReflectionTestUtils.setField(user, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(user.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(user.getPerson(), "updatedAt", Instant.now());

        final DetailUserDTO mappedDetailUser = UserMapper.toDetailUser(user);
        assertNotNull(mappedDetailUser);
        assertEquals(user.getCode(), mappedDetailUser.code());
        assertEquals(user.getUsername(), mappedDetailUser.username());
        assertEquals(user.isActive(), mappedDetailUser.active());
        assertNotNull(mappedDetailUser.person());
        assertEquals(LocalDateTime.ofInstant(user.getCreatedAt(), ZoneOffset.UTC), mappedDetailUser.createdAt());
        assertEquals(LocalDateTime.ofInstant(user.getUpdatedAt(), ZoneOffset.UTC), mappedDetailUser.updatedAt());
        assertEquals(user.getPerson().getCode(), mappedDetailUser.person().code());
        assertEquals(user.getPerson().getFirstName(), mappedDetailUser.person().firstName());
        assertEquals(user.getPerson().getLastName(), mappedDetailUser.person().lastName());
        assertEquals(user.getPerson().getEmail(), mappedDetailUser.person().email());
        assertEquals(LocalDateTime.ofInstant(user.getPerson().getCreatedAt(), ZoneOffset.UTC), mappedDetailUser.person().createdAt());
        assertEquals(LocalDateTime.ofInstant(user.getPerson().getUpdatedAt(), ZoneOffset.UTC), mappedDetailUser.person().updatedAt());
    }
}
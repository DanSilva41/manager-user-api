package com.challenge.manageruser.support.mapper;

import com.challenge.manageruser.model.dto.person.CreatePersonDTO;
import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.entity.backing.Department;
import com.challenge.manageruser.model.entity.backing.Person;
import com.challenge.manageruser.model.entity.security.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.challenge.manageruser.factory.FakerFactory.faker;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    @DisplayName("""
            DADO a solicitação de um novo usuário
            QUANDO mapear os campos para a entidade User
            DEVE refletir os valores corretamente
            """)
    @Test
    void shouldMapperToUserFromCreateUserDTO() {
        final CreatePersonDTO newPerson = new CreatePersonDTO(
                faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress()
        );
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), faker.number().positive(), newPerson);
        final Department department = Department.builder()
                .name(faker.commerce().department())
                .description(faker.marketing().buzzwords())
                .build();

        final User mappedUser = assertDoesNotThrow(() -> UserMapper.toUser(newUser, department));
        assertNotNull(mappedUser);
        assertEquals(newUser.username(), mappedUser.getUsername());
        assertEquals(newUser.password(), mappedUser.getPassword());
        assertNotNull(mappedUser.getPerson());
        assertEquals(newUser.person().firstName(), mappedUser.getPerson().getFirstName());
        assertEquals(newUser.person().lastName(), mappedUser.getPerson().getLastName());
        assertEquals(newUser.person().email(), mappedUser.getPerson().getEmail());
        assertNotNull(mappedUser.getDepartment());
        assertEquals(department.getName(), mappedUser.getDepartment().getName());
        assertEquals(department.getDescription(), mappedUser.getDepartment().getDescription());
    }

    @DisplayName("""
            DADO um usuário com entidade (User)
            QUANDO mapear os campos para a visualização detalhada
            DEVE refletir os valores corretamente
            """)
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
                .department(Department.builder()
                        .name(faker.commerce().department())
                        .name(faker.marketing().buzzwords())
                        .build())
                .build();
        ReflectionTestUtils.setField(user, "createdAt", Instant.now());
        ReflectionTestUtils.setField(user, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(user.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(user.getPerson(), "updatedAt", Instant.now());
        ReflectionTestUtils.setField(user.getDepartment(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(user.getDepartment(), "updatedAt", Instant.now());

        final DetailUserDTO mappedDetailUser = assertDoesNotThrow(() -> UserMapper.toDetailUser(user));
        assertNotNull(mappedDetailUser);
        assertEquals(user.getCode(), mappedDetailUser.code());
        assertEquals(user.getUsername(), mappedDetailUser.username());
        assertEquals(user.isActive(), mappedDetailUser.active());
        assertEquals(LocalDateTime.ofInstant(user.getCreatedAt(), ZoneOffset.UTC), mappedDetailUser.createdAt());
        assertEquals(LocalDateTime.ofInstant(user.getUpdatedAt(), ZoneOffset.UTC), mappedDetailUser.updatedAt());
        assertNotNull(mappedDetailUser.person());
        assertEquals(user.getPerson().getCode(), mappedDetailUser.person().code());
        assertEquals(user.getPerson().getFirstName(), mappedDetailUser.person().firstName());
        assertEquals(user.getPerson().getLastName(), mappedDetailUser.person().lastName());
        assertEquals(user.getPerson().getEmail(), mappedDetailUser.person().email());
        assertEquals(LocalDateTime.ofInstant(user.getPerson().getCreatedAt(), ZoneOffset.UTC), mappedDetailUser.person().createdAt());
        assertEquals(LocalDateTime.ofInstant(user.getPerson().getUpdatedAt(), ZoneOffset.UTC), mappedDetailUser.person().updatedAt());
        assertNotNull(mappedDetailUser.department());
        assertEquals(user.getDepartment().getName(), mappedDetailUser.department().name());
        assertEquals(user.getDepartment().getDescription(), mappedDetailUser.department().description());
        assertEquals(LocalDateTime.ofInstant(user.getDepartment().getCreatedAt(), ZoneOffset.UTC), mappedDetailUser.department().createdAt());
        assertEquals(LocalDateTime.ofInstant(user.getDepartment().getUpdatedAt(), ZoneOffset.UTC), mappedDetailUser.department().updatedAt());
    }
}
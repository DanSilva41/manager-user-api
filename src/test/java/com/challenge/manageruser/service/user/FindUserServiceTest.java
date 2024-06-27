package com.challenge.manageruser.service.user;

import com.challenge.manageruser.model.dto.FilterDTO;
import com.challenge.manageruser.model.dto.user.SimpleUserDTO;
import com.challenge.manageruser.model.entity.backing.Department;
import com.challenge.manageruser.model.entity.backing.Person;
import com.challenge.manageruser.model.entity.security.User;
import com.challenge.manageruser.repository.UserRepository;
import com.challenge.manageruser.support.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static com.challenge.manageruser.factory.FakerFactory.faker;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindUserServiceTest {

    @InjectMocks
    private FindUserService findUserService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("""
            DADO uma busca de usuários por filtro
            QUANDO forem encontrados usuários para os parâmetros informados
            DEVE retornar uma paginação de usuários
            """)
    @Test
    void shouldGetUsersPage() {
        final var filter = new FilterDTO(0, 20);
        final var foundUsers = Stream.of(
                User.builder()
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
                        .build(),
                User.builder()
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
                        .build()
        ).peek(user -> {
            ReflectionTestUtils.setField(user, "createdAt", Instant.now());
            ReflectionTestUtils.setField(user, "updatedAt", Instant.now());
        }).toList();

        when(userRepository.findAll(any())).thenReturn(new PageImpl<>(foundUsers));

        final Page<SimpleUserDTO> results = assertDoesNotThrow(() ->
                findUserService.getAllByFilter(filter)
        );

        final List<SimpleUserDTO> expectedUsers = foundUsers.stream().map(UserMapper::toSimpleUser).toList();

        assertNotNull(results);
        assertEquals(2, results.getTotalElements());
        assertEquals(1, results.getTotalPages());
        assertEquals(expectedUsers, results.getContent());

        verify(userRepository, times(1)).findAll(any());
    }
}
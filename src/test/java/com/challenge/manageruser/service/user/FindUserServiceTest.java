package com.challenge.manageruser.service.user;

import com.challenge.manageruser.exception.InvalidUserException;
import com.challenge.manageruser.exception.NotFoundUserException;
import com.challenge.manageruser.model.dto.FilterDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.challenge.manageruser.factory.FakerFactory.faker;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
            ReflectionTestUtils.setField(user, "code", faker.number().positive());
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

    @DisplayName("""
            DADO uma busca de usuários por filtro
            QUANDO NÃO forem encontrados usuários para os parâmetros informados
            DEVE retornar uma paginação vazia
            """)
    @Test
    void shouldGetEmptyUsersPage() {
        final var filter = new FilterDTO(0, 20);

        when(userRepository.findAll(any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        final Page<SimpleUserDTO> results = assertDoesNotThrow(() ->
                findUserService.getAllByFilter(filter)
        );

        assertNotNull(results);
        assertEquals(0, results.getTotalElements());
        assertEquals(1, results.getTotalPages());
        assertTrue(results.getContent().isEmpty());

        verify(userRepository, times(1)).findAll(any());
    }

    @DisplayName("""
            DADO uma busca de usuário por identificador único
            QUANDO for encontrado
            DEVE retornar informações detalhadas do mesmo
            """)
    @Test
    void shouldGetUserByCode() {
        final var userCode = faker.number().positive();
        final var foundUser = User.builder()
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
        ReflectionTestUtils.setField(foundUser, "code", userCode);
        ReflectionTestUtils.setField(foundUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(foundUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(foundUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(foundUser.getPerson(), "updatedAt", Instant.now());
        ReflectionTestUtils.setField(foundUser.getDepartment(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(foundUser.getDepartment(), "updatedAt", Instant.now());

        when(userRepository.findByCode(any())).thenReturn(Optional.of(foundUser));

        final DetailUserDTO detailUser = assertDoesNotThrow(() ->
                findUserService.getDetailByCode(userCode)
        );

        assertNotNull(detailUser);
        assertEquals(foundUser.getCode(), detailUser.code());
        assertEquals(foundUser.getUsername(), detailUser.username());
        assertEquals(foundUser.isActive(), detailUser.active());
        assertEquals(LocalDateTime.ofInstant(foundUser.getCreatedAt(), ZoneOffset.UTC), detailUser.createdAt());
        assertEquals(LocalDateTime.ofInstant(foundUser.getUpdatedAt(), ZoneOffset.UTC), detailUser.updatedAt());
        assertNotNull(detailUser.person());
        assertEquals(foundUser.getPerson().getCode(), detailUser.person().code());
        assertEquals(foundUser.getPerson().getFirstName(), detailUser.person().firstName());
        assertEquals(foundUser.getPerson().getLastName(), detailUser.person().lastName());
        assertEquals(foundUser.getPerson().getEmail(), detailUser.person().email());
        assertEquals(LocalDateTime.ofInstant(foundUser.getPerson().getCreatedAt(), ZoneOffset.UTC), detailUser.person().createdAt());
        assertEquals(LocalDateTime.ofInstant(foundUser.getPerson().getUpdatedAt(), ZoneOffset.UTC), detailUser.person().updatedAt());
        assertNotNull(detailUser.department());
        assertEquals(foundUser.getDepartment().getName(), detailUser.department().name());
        assertEquals(foundUser.getDepartment().getDescription(), detailUser.department().description());
        assertEquals(LocalDateTime.ofInstant(foundUser.getDepartment().getCreatedAt(), ZoneOffset.UTC), detailUser.department().createdAt());
        assertEquals(LocalDateTime.ofInstant(foundUser.getDepartment().getUpdatedAt(), ZoneOffset.UTC), detailUser.department().updatedAt());

        verify(userRepository, times(1)).findByCode(any());
    }

    @DisplayName("""
            DADO uma busca de usuário por identificador único
            QUANDO não for encontrado
            DEVE lançar exceção específica
            """)
    @Test
    void shouldThrowWhenUserNotFoundByCode() {
        final var userCode = faker.number().positive();
        when(userRepository.findByCode(any())).thenReturn(Optional.empty());

        final var expectedMessage = "User with identifier %d not found".formatted(userCode);
        assertThrows(
                NotFoundUserException.class,
                () -> findUserService.getDetailByCode(userCode),
                expectedMessage
        );

        verify(userRepository, times(1)).findByCode(any());
    }

    @DisplayName("""
            DADO uma validação de existência de usuário
            QUANDO NÃO for existente por username ou email
            NÃO DEVE lançar exceção
            """)
    @Test
    void shouldNotExistsUserByUsernameOrEmail() {
        when(userRepository.existsByUsernameOrPersonEmail(any(), any())).thenReturn(false);

        assertDoesNotThrow(() ->
                findUserService.validateAlreadyExists(faker.internet().username(), faker.internet().emailAddress())
        );

        verify(userRepository, times(1)).existsByUsernameOrPersonEmail(any(), any());
    }

    @DisplayName("""
            DADO uma validação de existência de usuário
            QUANDO for existente por username ou email
            DEVE lançar exceção
            """)
    @Test
    void shouldThrowWhenUserExistsByUsernameOrEmail() {
        when(userRepository.existsByUsernameOrPersonEmail(any(), any())).thenReturn(true);

        final var expectedMessage = "There's already a user with this username or person email";
        assertThrows(
                InvalidUserException.class,
                () -> findUserService.validateAlreadyExists(faker.internet().username(), faker.internet().emailAddress()),
                expectedMessage
        );

        verify(userRepository, times(1)).existsByUsernameOrPersonEmail(any(), any());
    }

    @DisplayName("""
            DADO uma validação de existência de usuário
            QUANDO for existente por identificador único
            NÃO DEVE lançar exceção
            """)
    @Test
    void shouldExistsUserByCode() {
        when(userRepository.existsByCode(any())).thenReturn(true);

        assertDoesNotThrow(() ->
                findUserService.validateIfNotExists(faker.number().positive())
        );

        verify(userRepository, times(1)).existsByCode(any());
    }

    @DisplayName("""
            DADO uma validação de existência de usuário
            QUANDO NÃO for existente por identificador único
            DEVE lançar exceção
            """)
    @Test
    void shouldThrowWhenNotExistsUserByCode() {
        when(userRepository.existsByCode(any())).thenReturn(false);

        final var userCode = faker.number().positive();
        final var expectedMessage = "User with identifier %d not found".formatted(userCode);

        assertThrows(
                NotFoundUserException.class,
                () -> findUserService.validateIfNotExists(userCode),
                expectedMessage
        );

        verify(userRepository, times(1)).existsByCode(any());
    }
}
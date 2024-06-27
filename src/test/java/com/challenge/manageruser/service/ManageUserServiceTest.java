package com.challenge.manageruser.service;

import com.challenge.manageruser.exception.InvalidUserException;
import com.challenge.manageruser.model.dto.person.CreatePersonDTO;
import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.entity.security.User;
import com.challenge.manageruser.repository.UserRepository;
import com.challenge.manageruser.support.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.challenge.manageruser.factory.FakerFactory.faker;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageUserServiceTest {

    @InjectMocks
    private ManageUserService manageUserService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("""
            DADO um novo usuário
            QUANDO mapeamento e persistência ocorrer com sucesso
            DEVE criar o usuário
            E retornar os detalhes do mesmo
            """)
    @Test
    void shouldCreateNewUser() {
        final CreatePersonDTO newPerson = new CreatePersonDTO(
                faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress()
        );
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), newPerson);

        final User expectedUser = UserMapper.toUser(newUser);
        ReflectionTestUtils.setField(expectedUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "updatedAt", Instant.now());

        when(userRepository.existsByPersonEmail(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(expectedUser);

        final DetailUserDTO savedDetailUser = assertDoesNotThrow(() ->
                manageUserService.create(newUser)
        );
        assertNotNull(savedDetailUser);
        assertEquals(expectedUser.getCode(), savedDetailUser.code());
        assertEquals(expectedUser.getUsername(), savedDetailUser.username());
        assertEquals(expectedUser.isActive(), savedDetailUser.active());
        assertNotNull(savedDetailUser.person());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getCreatedAt(), ZoneOffset.UTC), savedDetailUser.createdAt());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getUpdatedAt(), ZoneOffset.UTC), savedDetailUser.updatedAt());
        assertEquals(expectedUser.getPerson().getCode(), savedDetailUser.person().code());
        assertEquals(expectedUser.getPerson().getFirstName(), savedDetailUser.person().firstName());
        assertEquals(expectedUser.getPerson().getLastName(), savedDetailUser.person().lastName());
        assertEquals(expectedUser.getPerson().getEmail(), savedDetailUser.person().email());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getPerson().getCreatedAt(), ZoneOffset.UTC), savedDetailUser.person().createdAt());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getPerson().getUpdatedAt(), ZoneOffset.UTC), savedDetailUser.person().updatedAt());

        verify(userRepository, times(1)).existsByPersonEmail(any());
        verify(userRepository, times(1)).save(any());
    }

    @DisplayName("""
            DADO um novo usuário
            QUANDO for válido porém com email já existente
            NÃO DEVE criar o usuário
            E DEVE lançar exceção
            """)
    @Test
    void shouldThrowExceptionInUserCreationWhenAlreadyExistsUserByEmail() {
        final CreatePersonDTO newPerson = new CreatePersonDTO(
                faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress()
        );
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), newPerson);

        final User expectedUser = UserMapper.toUser(newUser);
        ReflectionTestUtils.setField(expectedUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "updatedAt", Instant.now());

        when(userRepository.existsByPersonEmail(any())).thenReturn(true);

        final var expectedMessage = "There's already a user with this email";
        assertThrows(
                InvalidUserException.class,
                () -> manageUserService.create(newUser),
                expectedMessage
        );

        verify(userRepository, times(1)).existsByPersonEmail(any());
        verify(userRepository, times(0)).save(any());
    }

    @DisplayName("""
            DADO um novo usuário
            QUANDO ocorrer alguma falha na persistência
            NÃO DEVE criar o usuário
            E DEVE lançar exceção
            """)
    @Test
    void shouldThrowExceptionWhenSomeErrorOccursInUserCreation() {
        final CreatePersonDTO newPerson = new CreatePersonDTO(
                faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress()
        );
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), newPerson);

        final User expectedUser = UserMapper.toUser(newUser);
        ReflectionTestUtils.setField(expectedUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "updatedAt", Instant.now());

        final var expectedMessage = "ERROR: duplicate key violates unique constraint 'un_person_email'";
        when(userRepository.existsByPersonEmail(any())).thenReturn(false);
        when(userRepository.save(any())).thenThrow(new IllegalArgumentException(expectedMessage));

        assertThrows(
                IllegalArgumentException.class,
                () -> manageUserService.create(newUser),
                expectedMessage
        );

        verify(userRepository, times(1)).existsByPersonEmail(any());
        verify(userRepository, times(1)).save(any());
    }
}
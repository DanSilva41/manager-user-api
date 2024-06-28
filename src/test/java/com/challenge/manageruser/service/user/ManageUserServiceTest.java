package com.challenge.manageruser.service.user;

import com.challenge.manageruser.exception.InvalidUserException;
import com.challenge.manageruser.exception.NotFoundUserException;
import com.challenge.manageruser.model.dto.person.CreatePersonDTO;
import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.entity.backing.Department;
import com.challenge.manageruser.model.entity.security.User;
import com.challenge.manageruser.repository.UserRepository;
import com.challenge.manageruser.service.department.FindDepartmentService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageUserServiceTest {

    @InjectMocks
    private ManageUserService manageUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FindDepartmentService findDepartmentService;

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
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), faker.commerce().department(), newPerson);
        final Department department = Department.builder()
                .name(newUser.departmentName())
                .description(faker.marketing().buzzwords())
                .build();
        ReflectionTestUtils.setField(department, "createdAt", Instant.now());
        ReflectionTestUtils.setField(department, "updatedAt", Instant.now());

        final User expectedUser = UserMapper.toUser(newUser, department);
        ReflectionTestUtils.setField(expectedUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "updatedAt", Instant.now());

        when(userRepository.existsByUsernameOrPersonEmail(any(), any())).thenReturn(false);
        when(findDepartmentService.getByName(any())).thenReturn(department);
        when(userRepository.save(any())).thenReturn(expectedUser);

        final DetailUserDTO savedDetailUser = assertDoesNotThrow(() ->
                manageUserService.create(newUser)
        );
        assertNotNull(savedDetailUser);
        assertEquals(expectedUser.getCode(), savedDetailUser.code());
        assertEquals(expectedUser.getUsername(), savedDetailUser.username());
        assertEquals(expectedUser.isActive(), savedDetailUser.active());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getCreatedAt(), ZoneOffset.UTC), savedDetailUser.createdAt());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getUpdatedAt(), ZoneOffset.UTC), savedDetailUser.updatedAt());
        assertNotNull(savedDetailUser.person());
        assertEquals(expectedUser.getPerson().getCode(), savedDetailUser.person().code());
        assertEquals(expectedUser.getPerson().getFirstName(), savedDetailUser.person().firstName());
        assertEquals(expectedUser.getPerson().getLastName(), savedDetailUser.person().lastName());
        assertEquals(expectedUser.getPerson().getEmail(), savedDetailUser.person().email());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getPerson().getCreatedAt(), ZoneOffset.UTC), savedDetailUser.person().createdAt());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getPerson().getUpdatedAt(), ZoneOffset.UTC), savedDetailUser.person().updatedAt());
        assertNotNull(savedDetailUser.department());
        assertEquals(expectedUser.getDepartment().getName(), savedDetailUser.department().name());
        assertEquals(expectedUser.getDepartment().getDescription(), savedDetailUser.department().description());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getDepartment().getCreatedAt(), ZoneOffset.UTC), savedDetailUser.department().createdAt());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getDepartment().getUpdatedAt(), ZoneOffset.UTC), savedDetailUser.department().updatedAt());

        verify(userRepository, times(1)).existsByUsernameOrPersonEmail(any(), any());
        verify(userRepository, times(1)).save(any());
    }

    @DisplayName("""
            DADO um novo usuário
            QUANDO for válido porém com username já existente
            NÃO DEVE criar o usuário
            E DEVE lançar exceção
            """)
    @Test
    void shouldThrowExceptionInUserCreationWhenAlreadyExistsUserByUsername() {
        final CreatePersonDTO newPerson = new CreatePersonDTO(
                faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress()
        );
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), faker.commerce().department(), newPerson);
        final Department department = Department.builder()
                .name(newUser.departmentName())
                .description(faker.marketing().buzzwords())
                .build();
        ReflectionTestUtils.setField(department, "createdAt", Instant.now());
        ReflectionTestUtils.setField(department, "updatedAt", Instant.now());

        final User expectedUser = UserMapper.toUser(newUser, department);
        ReflectionTestUtils.setField(expectedUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "updatedAt", Instant.now());

        when(userRepository.existsByUsernameOrPersonEmail(any(), any())).thenReturn(true);

        final var expectedMessage = "There's already a user with this username or person email";
        assertThrows(
                InvalidUserException.class,
                () -> manageUserService.create(newUser),
                expectedMessage
        );

        verify(userRepository, times(1)).existsByUsernameOrPersonEmail(any(), any());
        verify(userRepository, times(0)).save(any());
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
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), faker.commerce().department(), newPerson);
        final Department department = Department.builder()
                .name(newUser.departmentName())
                .description(faker.marketing().buzzwords())
                .build();
        ReflectionTestUtils.setField(department, "createdAt", Instant.now());
        ReflectionTestUtils.setField(department, "updatedAt", Instant.now());

        final User expectedUser = UserMapper.toUser(newUser, department);
        ReflectionTestUtils.setField(expectedUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "updatedAt", Instant.now());

        when(userRepository.existsByUsernameOrPersonEmail(any(), any())).thenReturn(true);

        final var expectedMessage = "There's already a user with this username or person email";
        assertThrows(
                InvalidUserException.class,
                () -> manageUserService.create(newUser),
                expectedMessage
        );

        verify(userRepository, times(1)).existsByUsernameOrPersonEmail(any(), any());
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
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), faker.commerce().department(), newPerson);
        final Department department = Department.builder()
                .name(newUser.departmentName())
                .description(faker.marketing().buzzwords())
                .build();

        final User expectedUser = UserMapper.toUser(newUser, department);
        ReflectionTestUtils.setField(expectedUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "updatedAt", Instant.now());

        final var expectedMessage = "ERROR: duplicate key violates unique constraint 'un_person_email'";
        when(userRepository.existsByUsernameOrPersonEmail(any(), any())).thenReturn(false);
        when(findDepartmentService.getByName(any())).thenReturn(department);
        when(userRepository.save(any())).thenThrow(new RuntimeException(expectedMessage));

        assertThrows(
                RuntimeException.class,
                () -> manageUserService.create(newUser),
                expectedMessage
        );

        verify(userRepository, times(1)).existsByUsernameOrPersonEmail(any(), any());
        verify(userRepository, times(1)).save(any());
    }

    @DisplayName("""
            DADO uma exclusão de usuário
            QUANDO for existente
            DEVE excluí-lo da base de dados
            """)
    @Test
    void shouldDeleteUser() {
        when(userRepository.existsByCode(any())).thenReturn(true);
        doNothing().when(userRepository).deleteByCode(any());

        assertDoesNotThrow(() ->
                manageUserService.delete(1)
        );

        verify(userRepository, times(1)).existsByCode(any());
        verify(userRepository, times(1)).deleteByCode(any());
    }

    @DisplayName("""
            DADO uma exclusão de usuário
            QUANDO não for encontrado por identificador único
            DEVE lançar exceção
            """)
    @Test
    void shouldThrowWhenUserNotFoundInDelete() {
        final var userCode = 1;
        when(userRepository.existsByCode(any())).thenReturn(false);

        final var expectedMessage = "User with identifier %d not found".formatted(userCode);
        assertThrows(
                NotFoundUserException.class,
                () -> manageUserService.delete(userCode),
                expectedMessage
        );

        verify(userRepository, times(1)).existsByCode(any());
        verify(userRepository, times(0)).deleteByCode(any());
    }
}
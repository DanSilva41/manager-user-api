package com.challenge.manageruser.service.user;

import com.challenge.manageruser.exception.InvalidUserException;
import com.challenge.manageruser.exception.NotFoundDepartmentException;
import com.challenge.manageruser.exception.NotFoundUserException;
import com.challenge.manageruser.model.dto.person.CreatePersonDTO;
import com.challenge.manageruser.model.dto.person.UpdatePersonDTO;
import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.dto.user.UpdateUserDTO;
import com.challenge.manageruser.model.entity.backing.Department;
import com.challenge.manageruser.model.entity.backing.Person;
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
import static org.mockito.Mockito.doThrow;
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

    @Mock
    private FindUserService findUserService;

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
        final Integer departmentCode = faker.number().positive();
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), departmentCode, newPerson);
        final Department department = Department.builder()
                .name(faker.commerce().department())
                .description(faker.marketing().buzzwords())
                .build();
        ReflectionTestUtils.setField(department, "code", departmentCode);
        ReflectionTestUtils.setField(department, "createdAt", Instant.now());
        ReflectionTestUtils.setField(department, "updatedAt", Instant.now());

        final User expectedUser = UserMapper.toUser(newUser, department);
        ReflectionTestUtils.setField(expectedUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "updatedAt", Instant.now());

        doNothing().when(findUserService).validateAlreadyExists(any(), any());
        when(findDepartmentService.getByCode(any())).thenReturn(department);
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

        verify(findUserService, times(1)).validateAlreadyExists(any(), any());
        verify(findDepartmentService, times(1)).getByCode(any());
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
        final Integer departmentCode = faker.number().positive();
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), departmentCode, newPerson);
        final Department department = Department.builder()
                .name(faker.commerce().department())
                .description(faker.marketing().buzzwords())
                .build();
        ReflectionTestUtils.setField(department, "code", departmentCode);
        ReflectionTestUtils.setField(department, "createdAt", Instant.now());
        ReflectionTestUtils.setField(department, "updatedAt", Instant.now());

        final User expectedUser = UserMapper.toUser(newUser, department);
        ReflectionTestUtils.setField(expectedUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "updatedAt", Instant.now());

        final var expectedMessage = "There's already a user with this username or person email";
        doThrow(new InvalidUserException(expectedMessage))
                .when(findUserService).validateAlreadyExists(any(), any());

        assertThrows(
                InvalidUserException.class,
                () -> manageUserService.create(newUser),
                expectedMessage
        );

        verify(findUserService, times(1)).validateAlreadyExists(any(), any());
        verify(findDepartmentService, times(0)).getByCode(any());
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
        final Integer departmentCode = faker.number().positive();
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), departmentCode, newPerson);
        final Department department = Department.builder()
                .name(faker.commerce().department())
                .description(faker.marketing().buzzwords())
                .build();
        ReflectionTestUtils.setField(department, "code", departmentCode);
        ReflectionTestUtils.setField(department, "createdAt", Instant.now());
        ReflectionTestUtils.setField(department, "updatedAt", Instant.now());

        final User expectedUser = UserMapper.toUser(newUser, department);
        ReflectionTestUtils.setField(expectedUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "updatedAt", Instant.now());

        final var expectedMessage = "There's already a user with this username or person email";
        doThrow(new InvalidUserException(expectedMessage))
                .when(findUserService).validateAlreadyExists(any(), any());

        assertThrows(
                InvalidUserException.class,
                () -> manageUserService.create(newUser),
                expectedMessage
        );

        verify(findUserService, times(1)).validateAlreadyExists(any(), any());
        verify(userRepository, times(0)).save(any());
    }

    @DisplayName("""
            DADO um novo usuário
            QUANDO for válido porém com identificador de departamento não existente
            NÃO DEVE criar o usuário
            E DEVE lançar exceção
            """)
    @Test
    void shouldThrowExceptionInUserCreationWhenDepartmentNotFound() {
        final CreatePersonDTO newPerson = new CreatePersonDTO(
                faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress()
        );
        final Integer departmentCode = faker.number().positive();
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), departmentCode, newPerson);


        final var expectedMessage = "Department %s not found".formatted(departmentCode);
        doNothing().when(findUserService).validateAlreadyExists(any(), any());
        when(findDepartmentService.getByCode(any())).thenThrow(new NotFoundDepartmentException(expectedMessage));

        assertThrows(
                InvalidUserException.class,
                () -> manageUserService.create(newUser),
                expectedMessage
        );

        verify(findUserService, times(1)).validateAlreadyExists(any(), any());
        verify(findDepartmentService, times(1)).getByCode(any());
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
        final Integer departmentCode = faker.number().positive();
        final CreateUserDTO newUser = new CreateUserDTO(faker.internet().username(), faker.internet().password(), departmentCode, newPerson);
        final Department department = Department.builder()
                .name(faker.commerce().department())
                .description(faker.marketing().buzzwords())
                .build();

        final User expectedUser = UserMapper.toUser(newUser, department);
        ReflectionTestUtils.setField(expectedUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "updatedAt", Instant.now());

        final var expectedMessage = "ERROR: duplicate key violates unique constraint 'un_person_email'";
        doNothing().when(findUserService).validateAlreadyExists(any(), any());
        when(findDepartmentService.getByCode(any())).thenReturn(department);
        when(userRepository.save(any())).thenThrow(new RuntimeException(expectedMessage));

        assertThrows(
                RuntimeException.class,
                () -> manageUserService.create(newUser),
                expectedMessage
        );

        verify(findUserService, times(1)).validateAlreadyExists(any(), any());
        verify(findDepartmentService, times(1)).getByCode(any());
        verify(userRepository, times(1)).save(any());
    }

    @DisplayName("""
            DADO uma atualização de usuário
            QUANDO mapeamento e persistência ocorrer com sucesso
            DEVE atualizar o usuário
            E retornar os detalhes do mesmo
            """)
    @Test
    void shouldUpdateNewUser() {
        final var userCode = faker.number().positive();
        final UpdatePersonDTO updatePerson = new UpdatePersonDTO(
                faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress()
        );
        final UpdateUserDTO updateUser = new UpdateUserDTO(faker.internet().username(), faker.internet().password(), faker.number().positive(), updatePerson);
        final User foundUser = User.builder()
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
        ReflectionTestUtils.setField(foundUser.getPerson(), "code", faker.number().positive());
        ReflectionTestUtils.setField(foundUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(foundUser.getPerson(), "updatedAt", Instant.now());
        ReflectionTestUtils.setField(foundUser.getDepartment(), "code", faker.number().positive());
        ReflectionTestUtils.setField(foundUser.getDepartment(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(foundUser.getDepartment(), "updatedAt", Instant.now());

        final Department newDepartment = Department.builder()
                .name(faker.commerce().department())
                .name(faker.marketing().buzzwords())
                .build();

        final User expectedUser = User.builder()
                .username(updateUser.username())
                .password(updateUser.password())
                .active(true)
                .person(Person.builder()
                        .firstName(updateUser.person().firstName())
                        .lastName(updateUser.person().lastName())
                        .email(updateUser.person().email())
                        .build())
                .department(Department.builder()
                        .name(newDepartment.getName())
                        .name(newDepartment.getName())
                        .build())
                .build();
        ReflectionTestUtils.setField(foundUser, "code", userCode);
        ReflectionTestUtils.setField(expectedUser, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser, "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "code", faker.number().positive());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getPerson(), "updatedAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getDepartment(), "code", faker.number().positive());
        ReflectionTestUtils.setField(expectedUser.getDepartment(), "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedUser.getDepartment(), "updatedAt", Instant.now());

        when(findUserService.getByCode(any())).thenReturn(foundUser);
        doNothing().when(findUserService).validateAlreadyExists(any(), any(), any());
        when(findDepartmentService.getByCode(any())).thenReturn(newDepartment);
        when(userRepository.save(any())).thenReturn(expectedUser);

        final DetailUserDTO updatedDetailUser = assertDoesNotThrow(() ->
                manageUserService.update(userCode, updateUser)
        );
        assertNotNull(updatedDetailUser);
        assertEquals(expectedUser.getCode(), updatedDetailUser.code());
        assertEquals(expectedUser.getUsername(), updatedDetailUser.username());
        assertEquals(expectedUser.isActive(), updatedDetailUser.active());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getCreatedAt(), ZoneOffset.UTC), updatedDetailUser.createdAt());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getUpdatedAt(), ZoneOffset.UTC), updatedDetailUser.updatedAt());
        assertNotNull(updatedDetailUser.person());
        assertEquals(expectedUser.getPerson().getCode(), updatedDetailUser.person().code());
        assertEquals(expectedUser.getPerson().getFirstName(), updatedDetailUser.person().firstName());
        assertEquals(expectedUser.getPerson().getLastName(), updatedDetailUser.person().lastName());
        assertEquals(expectedUser.getPerson().getEmail(), updatedDetailUser.person().email());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getPerson().getCreatedAt(), ZoneOffset.UTC), updatedDetailUser.person().createdAt());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getPerson().getUpdatedAt(), ZoneOffset.UTC), updatedDetailUser.person().updatedAt());
        assertNotNull(updatedDetailUser.department());
        assertEquals(expectedUser.getDepartment().getName(), updatedDetailUser.department().name());
        assertEquals(expectedUser.getDepartment().getDescription(), updatedDetailUser.department().description());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getDepartment().getCreatedAt(), ZoneOffset.UTC), updatedDetailUser.department().createdAt());
        assertEquals(LocalDateTime.ofInstant(expectedUser.getDepartment().getUpdatedAt(), ZoneOffset.UTC), updatedDetailUser.department().updatedAt());

        verify(findUserService, times(1)).getByCode(any());
        verify(findUserService, times(1)).validateAlreadyExists(any(), any(), any());
        verify(findDepartmentService, times(1)).getByCode(any());
        verify(userRepository, times(1)).save(any());
    }

    @DisplayName("""
            DADO uma exclusão de usuário
            QUANDO for existente
            DEVE excluí-lo da base de dados
            """)
    @Test
    void shouldDeleteUser() {
        doNothing().when(findUserService).validateIfNotExists(any());
        doNothing().when(userRepository).deleteByCode(any());

        assertDoesNotThrow(() ->
                manageUserService.delete(1)
        );

        verify(findUserService, times(1)).validateIfNotExists(any());
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
        final var expectedMessage = "User with identifier %d not found".formatted(userCode);
        doThrow(new NotFoundUserException(expectedMessage))
                .when(findUserService).validateIfNotExists(any());

        assertThrows(
                NotFoundUserException.class,
                () -> manageUserService.delete(userCode),
                expectedMessage
        );

        verify(findUserService, times(1)).validateIfNotExists(any());
        verify(userRepository, times(0)).deleteByCode(any());
    }
}
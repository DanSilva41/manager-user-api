package com.challenge.manageruser.service;

import com.challenge.manageruser.exception.InvalidDepartmentException;
import com.challenge.manageruser.model.dto.department.CreateDepartmentDTO;
import com.challenge.manageruser.model.dto.department.DetailDepartmentDTO;
import com.challenge.manageruser.model.entity.backing.Department;
import com.challenge.manageruser.repository.DepartmentRepository;
import com.challenge.manageruser.support.mapper.DepartmentMapper;
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
class ManageDepartmentServiceTest {

    @InjectMocks
    private ManageDepartmentService manageDepartmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    @DisplayName("""
            DADO um novo departamento
            QUANDO mapeamento e persistência ocorrer com sucesso
            DEVE criar o departamento
            E retornar os detalhes do mesmo
            """)
    @Test
    void shouldCreateNewDepartment() {
        final CreateDepartmentDTO newDepartment = new CreateDepartmentDTO(faker.commerce().department(), faker.marketing().buzzwords());

        final Department expectedDepartment = DepartmentMapper.toDepartment(newDepartment);
        ReflectionTestUtils.setField(expectedDepartment, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedDepartment, "updatedAt", Instant.now());

        when(departmentRepository.existsByName(any())).thenReturn(false);
        when(departmentRepository.save(any())).thenReturn(expectedDepartment);

        final DetailDepartmentDTO savedDetailDepartment = assertDoesNotThrow(() ->
                manageDepartmentService.create(newDepartment)
        );
        assertNotNull(savedDetailDepartment);
        assertEquals(expectedDepartment.getName(), savedDetailDepartment.name());
        assertEquals(expectedDepartment.getDescription(), savedDetailDepartment.description());
        assertEquals(LocalDateTime.ofInstant(expectedDepartment.getCreatedAt(), ZoneOffset.UTC), savedDetailDepartment.createdAt());
        assertEquals(LocalDateTime.ofInstant(expectedDepartment.getUpdatedAt(), ZoneOffset.UTC), savedDetailDepartment.updatedAt());

        verify(departmentRepository, times(1)).existsByName(any());
        verify(departmentRepository, times(1)).save(any());
    }

    @DisplayName("""
            DADO um novo departamento
            QUANDO for válido porém com nome já existente
            NÃO DEVE criar o departamento
            E DEVE lançar exceção
            """)
    @Test
    void shouldThrowExceptionInDepartmentCreationWhenAlreadyExistsDepartmentByName() {
        final CreateDepartmentDTO newDepartment = new CreateDepartmentDTO(faker.commerce().department(), faker.marketing().buzzwords());

        final Department expectedDepartment = DepartmentMapper.toDepartment(newDepartment);
        ReflectionTestUtils.setField(expectedDepartment, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedDepartment, "updatedAt", Instant.now());

        when(departmentRepository.existsByName(any())).thenReturn(true);

        final var expectedMessage = "There's already a department with this name";
        assertThrows(
                InvalidDepartmentException.class,
                () -> manageDepartmentService.create(newDepartment),
                expectedMessage
        );

        verify(departmentRepository, times(1)).existsByName(any());
        verify(departmentRepository, times(0)).save(any());
    }

    @DisplayName("""
            DADO um novo departamento
            QUANDO ocorrer alguma falha na persistência
            NÃO DEVE criar o departamento
            E DEVE lançar exceção
            """)
    @Test
    void shouldThrowExceptionWhenSomeErrorOccursInDepartmentCreation() {
        final CreateDepartmentDTO newDepartment = new CreateDepartmentDTO(faker.commerce().department(), faker.marketing().buzzwords());

        final Department expectedDepartment = DepartmentMapper.toDepartment(newDepartment);
        ReflectionTestUtils.setField(expectedDepartment, "createdAt", Instant.now());
        ReflectionTestUtils.setField(expectedDepartment, "updatedAt", Instant.now());

        final var expectedMessage = "ConnectException: Conexão recusada";
        when(departmentRepository.existsByName(any())).thenReturn(false);
        when(departmentRepository.save(any())).thenThrow(new RuntimeException(expectedMessage));

        assertThrows(
                RuntimeException.class,
                () -> manageDepartmentService.create(newDepartment),
                expectedMessage
        );

        verify(departmentRepository, times(1)).existsByName(any());
        verify(departmentRepository, times(1)).save(any());
    }
}
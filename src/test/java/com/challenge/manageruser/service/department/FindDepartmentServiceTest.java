package com.challenge.manageruser.service.department;

import com.challenge.manageruser.exception.NotFoundDepartmentException;
import com.challenge.manageruser.model.entity.backing.Department;
import com.challenge.manageruser.repository.DepartmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

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
class FindDepartmentServiceTest {

    @InjectMocks
    private FindDepartmentService findDepartmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    @DisplayName("""
            DADO uma busca de departamento por nome
            QUANDO for encontrado
            DEVE retornar informações detalhadas do mesmo
            """)
    @Test
    void shouldGetDepartmentByName() {
        final var departmentName = faker.commerce().department();
        final var foundDepartment = Department.builder()
                .name(departmentName)
                .name(faker.marketing().buzzwords())
                .build();
        ReflectionTestUtils.setField(foundDepartment, "createdAt", Instant.now());
        ReflectionTestUtils.setField(foundDepartment, "updatedAt", Instant.now());

        when(departmentRepository.findByName(any())).thenReturn(Optional.of(foundDepartment));

        final Department returnedDepartment = assertDoesNotThrow(() ->
                findDepartmentService.getByName(departmentName)
        );

        assertNotNull(returnedDepartment);
        assertEquals(foundDepartment.getName(), returnedDepartment.getName());
        assertEquals(foundDepartment.getDescription(), returnedDepartment.getDescription());
        assertEquals(foundDepartment.getCreatedAt(), returnedDepartment.getCreatedAt());
        assertEquals(foundDepartment.getUpdatedAt(), returnedDepartment.getUpdatedAt());

        verify(departmentRepository, times(1)).findByName(any());
    }

    @DisplayName("""
            DADO uma busca de departamento por identificador único
            QUANDO não for encontrado
            DEVE lançar exceção específica
            """)
    @Test
    void shouldThrowWhenDepartmentNotFoundByName() {
        final var departmentName = faker.commerce().department();
        when(departmentRepository.findByName(any())).thenReturn(Optional.empty());

        final var expectedMessage = "Department %s not found".formatted(departmentName);
        assertThrows(
                NotFoundDepartmentException.class,
                () -> findDepartmentService.getByName(departmentName),
                expectedMessage
        );

        verify(departmentRepository, times(1)).findByName(any());
    }
}
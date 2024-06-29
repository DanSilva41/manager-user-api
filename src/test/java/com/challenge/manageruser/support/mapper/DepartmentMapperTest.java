package com.challenge.manageruser.support.mapper;

import com.challenge.manageruser.model.dto.department.CreateDepartmentDTO;
import com.challenge.manageruser.model.dto.department.DetailDepartmentDTO;
import com.challenge.manageruser.model.entity.backing.Department;
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

class DepartmentMapperTest {

    @DisplayName("""
            DADO a solicitação de um novo departamento
            QUANDO mapear os campos para a entidade Department
            DEVE refletir os valores corretamente
            """)
    @Test
    void shouldMapperToDepartmentFromCreateDepartmentDTO() {
        final CreateDepartmentDTO newDepartment = new CreateDepartmentDTO(faker.commerce().department(), faker.marketing().buzzwords());

        final Department mappedDepartment = assertDoesNotThrow(() -> DepartmentMapper.toDepartment(newDepartment));
        assertNotNull(mappedDepartment);
        assertEquals(newDepartment.name(), mappedDepartment.getName());
        assertEquals(newDepartment.description(), mappedDepartment.getDescription());
    }

    @DisplayName("""
            DADO um departamento com entidade (Department)
            QUANDO mapear os campos para a visualização detalhada
            DEVE refletir os valores corretamente
            """)
    @Test
    void shouldMapperToDetailDepartmentFromDepartment() {
        final var departmentCode = faker.number().positive();
        final Department department = Department.builder().name(faker.commerce().department()).description(faker.marketing().buzzwords()).build();
        ReflectionTestUtils.setField(department, "code", departmentCode);
        ReflectionTestUtils.setField(department, "createdAt", Instant.now());
        ReflectionTestUtils.setField(department, "updatedAt", Instant.now());

        final DetailDepartmentDTO mappedDetailDepartment = assertDoesNotThrow(() -> DepartmentMapper.toDetailDepartment(department));
        assertNotNull(mappedDetailDepartment);
        assertEquals(departmentCode, mappedDetailDepartment.code());
        assertEquals(department.getName(), mappedDetailDepartment.name());
        assertEquals(department.getDescription(), mappedDetailDepartment.description());
        assertEquals(LocalDateTime.ofInstant(department.getCreatedAt(), ZoneOffset.UTC), mappedDetailDepartment.createdAt());
        assertEquals(LocalDateTime.ofInstant(department.getUpdatedAt(), ZoneOffset.UTC), mappedDetailDepartment.updatedAt());
    }
}
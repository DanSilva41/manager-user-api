package com.challenge.manageruser.support.mapper;

import com.challenge.manageruser.model.dto.department.CreateDepartmentDTO;
import com.challenge.manageruser.model.dto.department.DetailDepartmentDTO;
import com.challenge.manageruser.model.entity.backing.Department;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DepartmentMapper {

    private DepartmentMapper() throws IllegalAccessException {
        throw new IllegalAccessException("Do not instantiate this class, use statically");
    }

    public static Department toDepartment(final CreateDepartmentDTO newDepartment) {
        return Department.builder()
                .name(newDepartment.name())
                .description(newDepartment.description())
                .build();
    }

    public static DetailDepartmentDTO toDetailDepartment(final Department department) {
        return DetailDepartmentDTO.builder()
                .code(department.getCode())
                .name(department.getName())
                .description(department.getDescription())
                .createdAt(LocalDateTime.ofInstant(department.getCreatedAt(), ZoneOffset.UTC))
                .updatedAt(LocalDateTime.ofInstant(department.getUpdatedAt(), ZoneOffset.UTC))
                .build();
    }
}

package com.challenge.manageruser.service;

import com.challenge.manageruser.exception.InvalidDepartmentException;
import com.challenge.manageruser.model.dto.department.CreateDepartmentDTO;
import com.challenge.manageruser.model.dto.department.DetailDepartmentDTO;
import com.challenge.manageruser.repository.DepartmentRepository;
import com.challenge.manageruser.support.mapper.DepartmentMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ManageDepartmentService {

    private final DepartmentRepository departmentRepository;

    public ManageDepartmentService(final DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public DetailDepartmentDTO create(@Valid final CreateDepartmentDTO newDepartment) {
        validateIfExists(newDepartment.name());

        final var savedDepartment = departmentRepository.save(
                DepartmentMapper.toDepartment(newDepartment)
        );

        return DepartmentMapper.toDetailDepartment(savedDepartment);
    }

    private void validateIfExists(final String departmentName) {
        if (departmentRepository.existsByName(departmentName)) {
            throw new InvalidDepartmentException("There's already a department with this name");
        }
    }
}

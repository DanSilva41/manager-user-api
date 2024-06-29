package com.challenge.manageruser.service.department;

import com.challenge.manageruser.exception.InvalidDepartmentException;
import com.challenge.manageruser.exception.NotFoundDepartmentException;
import com.challenge.manageruser.model.entity.backing.Department;
import com.challenge.manageruser.repository.DepartmentRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class FindDepartmentService {

    private static final Logger log = LoggerFactory.getLogger(FindDepartmentService.class);

    private final DepartmentRepository departmentRepository;

    public FindDepartmentService(final DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department getByCode(@NotNull final Integer departmentCode) {
        log.info("m=searching department by code, departmentCode={}", departmentCode);
        return departmentRepository.findByCode(departmentCode)
                .orElseThrow(() -> new NotFoundDepartmentException("Department %s not found".formatted(departmentCode)));
    }

    public void validateIfAlreadyExists(final String departmentName) {
        log.info("m=validating if department already exists, departmentName={}", departmentName);
        if (departmentRepository.existsByName(departmentName)) {
            throw new InvalidDepartmentException("There's already a department with this name");
        }
    }
}

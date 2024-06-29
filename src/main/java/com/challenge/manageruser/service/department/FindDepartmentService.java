package com.challenge.manageruser.service.department;

import com.challenge.manageruser.exception.NotFoundDepartmentException;
import com.challenge.manageruser.model.entity.backing.Department;
import com.challenge.manageruser.repository.DepartmentRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class FindDepartmentService {

    private final DepartmentRepository departmentRepository;

    public FindDepartmentService(final DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department getByCode(@NotNull final Integer departmentCode) {
        return departmentRepository.findByCode(departmentCode)
                .orElseThrow(() -> new NotFoundDepartmentException("Department %s not found".formatted(departmentCode)));
    }
}

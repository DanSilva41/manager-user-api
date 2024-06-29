package com.challenge.manageruser.service.department;

import com.challenge.manageruser.model.dto.department.CreateDepartmentDTO;
import com.challenge.manageruser.model.dto.department.DetailDepartmentDTO;
import com.challenge.manageruser.repository.DepartmentRepository;
import com.challenge.manageruser.support.mapper.DepartmentMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ManageDepartmentService {

    private static final Logger log = LoggerFactory.getLogger(ManageDepartmentService.class);

    private final DepartmentRepository departmentRepository;
    private final FindDepartmentService findDepartmentService;

    public ManageDepartmentService(final DepartmentRepository departmentRepository,
                                   final FindDepartmentService findDepartmentService) {
        this.departmentRepository = departmentRepository;
        this.findDepartmentService = findDepartmentService;
    }

    public DetailDepartmentDTO create(@Valid final CreateDepartmentDTO newDepartment) {
        findDepartmentService.validateIfAlreadyExists(newDepartment.name());

        log.info("m=persisting new department, departmentName={}", newDepartment.name());
        final var savedDepartment = departmentRepository.save(
                DepartmentMapper.toDepartment(newDepartment)
        );

        return DepartmentMapper.toDetailDepartment(savedDepartment);
    }
}

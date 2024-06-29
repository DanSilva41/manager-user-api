package com.challenge.manageruser.web.rest;

import com.challenge.manageruser.model.dto.department.CreateDepartmentDTO;
import com.challenge.manageruser.model.dto.department.DetailDepartmentDTO;
import com.challenge.manageruser.service.department.ManageDepartmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/v1/department")
@Validated
public class DepartmentResource {

    private static final Logger log = LoggerFactory.getLogger(DepartmentResource.class);

    private final ManageDepartmentService manageDepartmentService;

    public DepartmentResource(ManageDepartmentService manageDepartmentService) {
        this.manageDepartmentService = manageDepartmentService;
    }

    @PostMapping
    public ResponseEntity<DetailDepartmentDTO> createDepartment(@Valid @RequestBody final CreateDepartmentDTO newDepartment) {
        log.info("m=POST request to create new department, name={}", newDepartment.name());
        final var savedDepartment = manageDepartmentService.create(newDepartment);

        log.info("m=new department saved, departmentCode={}, name={}", savedDepartment.code(), savedDepartment.name());
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{name}").buildAndExpand(savedDepartment.name()).toUri())
                .body(savedDepartment);
    }
}

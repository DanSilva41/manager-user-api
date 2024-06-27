package com.challenge.manageruser.web.rest;

import com.challenge.manageruser.model.dto.department.CreateDepartmentDTO;
import com.challenge.manageruser.model.dto.department.DetailDepartmentDTO;
import com.challenge.manageruser.service.ManageDepartmentService;
import jakarta.validation.Valid;
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

    private final ManageDepartmentService manageDepartmentService;

    public DepartmentResource(ManageDepartmentService manageDepartmentService) {
        this.manageDepartmentService = manageDepartmentService;
    }

    @PostMapping
    public ResponseEntity<DetailDepartmentDTO> createDepartment(@Valid @RequestBody final CreateDepartmentDTO newDepartment) {
        final var savedDepartment = manageDepartmentService.create(newDepartment);

        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{name}").buildAndExpand(savedDepartment.name()).toUri())
                .body(savedDepartment);
    }
}

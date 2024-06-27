package com.challenge.manageruser.web.rest;

import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.service.ManageUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/v1/user")
@Validated
public class UserResource {

    private final ManageUserService manageUserService;

    public UserResource(ManageUserService manageUserService) {
        this.manageUserService = manageUserService;
    }

    @PostMapping
    public ResponseEntity<DetailUserDTO> createUser(@Valid @RequestBody final CreateUserDTO newUser) {
        final var savedUser = manageUserService.create(newUser);

        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.code()).toUri())
                .body(savedUser);
    }
}

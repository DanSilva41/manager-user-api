package com.challenge.manageruser.web.rest;

import com.challenge.manageruser.model.dto.FilterDTO;
import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.dto.user.SimpleUserDTO;
import com.challenge.manageruser.service.user.FindUserService;
import com.challenge.manageruser.service.user.ManageUserService;
import com.challenge.manageruser.web.common.SnakePage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/v1/user")
@Validated
public class UserResource {

    private final ManageUserService manageUserService;
    private final FindUserService findUserService;

    public UserResource(final ManageUserService manageUserService,
                        final FindUserService findUserService) {
        this.manageUserService = manageUserService;
        this.findUserService = findUserService;
    }

    @PostMapping
    public ResponseEntity<DetailUserDTO> create(@Valid @RequestBody final CreateUserDTO newUser) {
        final var savedUser = manageUserService.create(newUser);

        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{code}").buildAndExpand(savedUser.code()).toUri())
                .body(savedUser);
    }

    @GetMapping
    public ResponseEntity<SnakePage<SimpleUserDTO>> getAllByFilter(
            @RequestParam(required = false, defaultValue = "0") @Min(0) final Integer page,
            @RequestParam(required = false, defaultValue = "20") @Min(1) final Integer size
    ) {
        final Page<SimpleUserDTO> foundUsers = findUserService.getAllByFilter(new FilterDTO(page, size));
        return ResponseEntity.ok(new SnakePage<>(foundUsers));
    }

    @GetMapping("/{code}")
    public ResponseEntity<DetailUserDTO> getByCode(@PathVariable final Integer code) {
        final DetailUserDTO detailUser = findUserService.getByCode(code);
        return ResponseEntity.ok(detailUser);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteByCode(@PathVariable final Integer code) {
        manageUserService.delete(code);
        return ResponseEntity.noContent().build();
    }
}

package com.challenge.manageruser.web.rest;

import com.challenge.manageruser.model.dto.FilterDTO;
import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.dto.user.SimpleUserDTO;
import com.challenge.manageruser.model.dto.user.UpdateUserDTO;
import com.challenge.manageruser.service.user.FindUserService;
import com.challenge.manageruser.service.user.ManageUserService;
import com.challenge.manageruser.web.common.SnakePage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/v1/user")
@Validated
public class UserResource {

    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

    private final ManageUserService manageUserService;
    private final FindUserService findUserService;

    public UserResource(final ManageUserService manageUserService,
                        final FindUserService findUserService) {
        this.manageUserService = manageUserService;
        this.findUserService = findUserService;
    }

    @PostMapping
    public ResponseEntity<DetailUserDTO> create(@Valid @RequestBody final CreateUserDTO newUser) {
        log.info("m=POST request to create new user, username={}, email={}", newUser.username(), newUser.person().email());
        final var savedUser = manageUserService.create(newUser);

        log.info("m=new user saved, userCode={}, username={}, email={}", savedUser.code(), savedUser.username(), savedUser.person().email());
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{code}").buildAndExpand(savedUser.code()).toUri())
                .body(savedUser);
    }

    @GetMapping
    public ResponseEntity<SnakePage<SimpleUserDTO>> getAllByFilter(
            @RequestParam(required = false, defaultValue = "0") @Min(0) final Integer page,
            @RequestParam(required = false, defaultValue = "20") @Min(1) final Integer size
    ) {
        log.info("m=GET request to search for all users by filter, page={}, size={}", page, size);
        final SnakePage<SimpleUserDTO> foundUsers = new SnakePage<>(
                findUserService.getAllByFilter(new FilterDTO(page, size))
        );
        log.info("m={} users found, page={}, size={}", foundUsers.totalElements(), page, size);
        return ResponseEntity.ok(foundUsers);
    }

    @GetMapping("/{code}")
    public ResponseEntity<DetailUserDTO> getByCode(@PathVariable final Integer code) {
        log.info("m=GET request to search a user by code, userCode={}", code);
        final DetailUserDTO detailUser = findUserService.getDetailByCode(code);

        log.info("m=found user by code, userCode={}", code);
        return ResponseEntity.ok(detailUser);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteByCode(@PathVariable final Integer code) {
        log.info("m=DELETE request to remove a user by code, userCode={}", code);
        manageUserService.delete(code);

        log.info("m=deleted user by code, userCode={}", code);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{code}")
    public ResponseEntity<DetailUserDTO> update(@PathVariable final Integer code,
                                                @Valid @RequestBody final UpdateUserDTO updateUser) {
        log.info("m=PUT request to update a user, userCode={}", code);
        final var updatedUser = manageUserService.update(code, updateUser);

        log.info("m=updated user, userCode={}", code);
        return ResponseEntity.ok(updatedUser);
    }
}

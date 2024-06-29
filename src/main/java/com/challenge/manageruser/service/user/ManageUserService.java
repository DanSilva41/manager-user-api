package com.challenge.manageruser.service.user;

import com.challenge.manageruser.exception.InvalidUserException;
import com.challenge.manageruser.exception.NotFoundDepartmentException;
import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.dto.user.UpdateUserDTO;
import com.challenge.manageruser.model.entity.backing.Department;
import com.challenge.manageruser.repository.UserRepository;
import com.challenge.manageruser.service.department.FindDepartmentService;
import com.challenge.manageruser.support.mapper.UserMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ManageUserService {

    private static final Logger log = LoggerFactory.getLogger(ManageUserService.class);

    private final UserRepository userRepository;
    private final FindUserService findUserService;
    private final FindDepartmentService findDepartmentService;

    public ManageUserService(final UserRepository userRepository, final FindUserService findUserService, final FindDepartmentService findDepartmentService) {
        this.userRepository = userRepository;
        this.findUserService = findUserService;
        this.findDepartmentService = findDepartmentService;
    }

    public DetailUserDTO create(@Valid final CreateUserDTO newUser) {
        findUserService.validateAlreadyExists(newUser.username(), newUser.person().email());

        final var userDepartment = this.findUserDepartment(newUser.departmentCode());

        log.info("m=persisting new user, username={}, email={}", newUser.username(), newUser.person().email());
        final var savedUser = userRepository.save(UserMapper.toUser(newUser, userDepartment));
        return UserMapper.toDetailUser(savedUser);
    }

    public DetailUserDTO update(final Integer code, @Valid final UpdateUserDTO updateUser) {
        final var foundUser = findUserService.getByCode(code);

        // se atualização de usuário possui username ou email diferente do atual, valida duplicidade
        if (!foundUser.getUsername().equals(updateUser.username()) || !foundUser.getPerson().getEmail().equals(updateUser.person().email())) {
            findUserService.validateAlreadyExists(updateUser.username(), updateUser.person().email(), foundUser.getCode());
        }

        // se atualização de usuário possui departamento diferente do atual, atribui o novo departamento
        if (!foundUser.getDepartment().getCode().equals(updateUser.departmentCode())) {
            final var newDepartmentUser = this.findUserDepartment(updateUser.departmentCode());
            log.info("m=assigning new department to user, userCode={}, departmentCode={},", foundUser.getCode(), newDepartmentUser.getCode());
            foundUser.setDepartment(newDepartmentUser);
        }

        log.info("m=updating user, userCode={}", foundUser.getCode());
        final var updatedUser = userRepository.save(UserMapper.toUser(updateUser, foundUser));
        return UserMapper.toDetailUser(updatedUser);
    }

    public void delete(final Integer code) {
        findUserService.validateIfNotExists(code);

        log.info("m=deleting user, userCode={}", code);
        userRepository.deleteByCode(code);
    }

    private Department findUserDepartment(final Integer code) {
        try {
            return findDepartmentService.getByCode(code);
        } catch (NotFoundDepartmentException exception) {
            log.error("m=department not found in user creation, departmentCode={}", code);
            throw new InvalidUserException(exception.getMessage());
        }
    }
}

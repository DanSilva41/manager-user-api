package com.challenge.manageruser.service.user;

import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.dto.user.UpdateUserDTO;
import com.challenge.manageruser.repository.UserRepository;
import com.challenge.manageruser.service.department.FindDepartmentService;
import com.challenge.manageruser.support.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ManageUserService {

    private final UserRepository userRepository;
    private final FindUserService findUserService;
    private final FindDepartmentService findDepartmentService;

    public ManageUserService(final UserRepository userRepository,
                             final FindUserService findUserService,
                             final FindDepartmentService findDepartmentService) {
        this.userRepository = userRepository;
        this.findUserService = findUserService;
        this.findDepartmentService = findDepartmentService;
    }

    public DetailUserDTO create(@Valid final CreateUserDTO newUser) {
        findUserService.alreadyExists(newUser.username(), newUser.person().email());

        final var department = findDepartmentService.getByName(newUser.departmentName());

        final var savedUser = userRepository.save(
                UserMapper.toUser(newUser, department)
        );
        return UserMapper.toDetailUser(savedUser);
    }

    public DetailUserDTO update(final Integer code, @Valid final UpdateUserDTO updateUser) {
        final var foundUser = findUserService.getByCode(code);
        // se atualização de usuário possui departamento diferente do atual, atribui o novo departamento
        if (!foundUser.getDepartment().getName().equals(updateUser.departmentName())) {
            final var newDepartmentUser = findDepartmentService.getByName(updateUser.departmentName());
            foundUser.setDepartment(newDepartmentUser);
        }

        final var updatedUser = userRepository.save(
                UserMapper.toUser(updateUser, foundUser)
        );
        return UserMapper.toDetailUser(updatedUser);
    }

    public void delete(final Integer code) {
        findUserService.validateIfNotExists(code);
        userRepository.deleteByCode(code);
    }
}

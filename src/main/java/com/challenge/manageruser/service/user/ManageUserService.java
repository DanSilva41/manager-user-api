package com.challenge.manageruser.service.user;

import com.challenge.manageruser.exception.InvalidUserException;
import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
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
    private final FindDepartmentService findDepartmentService;

    public ManageUserService(final UserRepository userRepository,
                             final FindDepartmentService findDepartmentService) {
        this.userRepository = userRepository;
        this.findDepartmentService = findDepartmentService;
    }

    public DetailUserDTO create(@Valid final CreateUserDTO newUser) {
        validateIfExists(newUser);

        final var department = findDepartmentService.getByName(newUser.departmentName());

        final var savedUser = userRepository.save(
                UserMapper.toUser(newUser, department)
        );
        return UserMapper.toDetailUser(savedUser);
    }

    private void validateIfExists(@Valid final CreateUserDTO newUser) {
        if (userRepository.existsByUsernameOrPersonEmail(newUser.username(), newUser.person().email())) {
            throw new InvalidUserException("There's already a user with this username or person email");
        }
    }
}

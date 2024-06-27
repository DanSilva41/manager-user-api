package com.challenge.manageruser.service;

import com.challenge.manageruser.exception.InvalidUserException;
import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.repository.UserRepository;
import com.challenge.manageruser.support.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ManageUserService {

    private final UserRepository userRepository;

    public ManageUserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public DetailUserDTO create(@Valid final CreateUserDTO newUser) {
        validateIfExists(newUser.person().email());

        final var savedUser = userRepository.save(
                UserMapper.toUser(newUser)
        );

        return UserMapper.toDetailUser(savedUser);
    }

    private void validateIfExists(final String email) {
        if (userRepository.existsByPersonEmail(email)) {
            throw new InvalidUserException("There's already a user with this email");
        }
    }
}

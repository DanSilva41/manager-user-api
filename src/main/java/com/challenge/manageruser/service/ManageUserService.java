package com.challenge.manageruser.service;

import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.repository.UserRepository;
import com.challenge.manageruser.support.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class ManageUserService {

    private final UserRepository userRepository;

    public ManageUserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public DetailUserDTO create(final CreateUserDTO newUser) {
        final var savedUser = userRepository.save(
                UserMapper.toUser(newUser)
        );

        return UserMapper.toDetailUser(savedUser);
    }
}

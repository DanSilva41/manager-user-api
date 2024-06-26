package com.challenge.manageruser.service;

import com.challenge.manageruser.model.dto.person.DetailPersonDTO;
import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.entity.backing.Person;
import com.challenge.manageruser.model.entity.security.User;
import com.challenge.manageruser.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class ManageUserService {

    private final UserRepository userRepository;

    public ManageUserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public DetailUserDTO create(final CreateUserDTO newUser) {
        final var savedUser = userRepository.save(
                User.builder()
                        .username(newUser.getUsername())
                        .password(newUser.getPassword())
                        .person(Person.builder()
                                .firstName(newUser.getPerson().getFirstName())
                                .lastName(newUser.getPerson().getLastName())
                                .email(newUser.getPerson().getEmail())
                                .build())
                        .active(true)
                        .build()
        );

        return DetailUserDTO.builder()
                .code(savedUser.getCode())
                .username(savedUser.getUsername())
                .active(savedUser.isActive())
                .person(DetailPersonDTO.builder()
                        .code(savedUser.getPerson().getCode())
                        .firstName(savedUser.getPerson().getFirstName())
                        .lastName(savedUser.getPerson().getLastName())
                        .email(savedUser.getPerson().getEmail())
                        .createdAt(LocalDateTime.ofInstant(savedUser.getPerson().getCreatedAt(), ZoneOffset.UTC))
                        .updatedAt(LocalDateTime.ofInstant(savedUser.getPerson().getUpdatedAt(), ZoneOffset.UTC))
                        .build())
                .createdAt(LocalDateTime.ofInstant(savedUser.getCreatedAt(), ZoneOffset.UTC))
                .updatedAt(LocalDateTime.ofInstant(savedUser.getUpdatedAt(), ZoneOffset.UTC))
                .build();
    }
}

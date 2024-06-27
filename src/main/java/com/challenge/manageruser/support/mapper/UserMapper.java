package com.challenge.manageruser.support.mapper;

import com.challenge.manageruser.model.dto.user.CreateUserDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.dto.user.SimpleUserDTO;
import com.challenge.manageruser.model.entity.backing.Department;
import com.challenge.manageruser.model.entity.security.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class UserMapper {

    private UserMapper() throws IllegalAccessException {
        throw new IllegalAccessException("Do not instantiate this class, use statically");
    }

    public static User toUser(final CreateUserDTO newUser, final Department department) {
        return User.builder()
                .username(newUser.username())
                .password(newUser.password())
                .person(PersonMapper.toPerson(newUser.person()))
                .department(department)
                .active(true)
                .build();
    }

    public static DetailUserDTO toDetailUser(final User user) {
        return DetailUserDTO.builder()
                .code(user.getCode())
                .username(user.getUsername())
                .active(user.isActive())
                .person(PersonMapper.toDetailPerson(user.getPerson()))
                .department(DepartmentMapper.toDetailDepartment(user.getDepartment()))
                .createdAt(LocalDateTime.ofInstant(user.getCreatedAt(), ZoneOffset.UTC))
                .updatedAt(LocalDateTime.ofInstant(user.getUpdatedAt(), ZoneOffset.UTC))
                .build();
    }

    public static SimpleUserDTO toSimpleUser(final User user) {
        return SimpleUserDTO.builder()
                .code(user.getCode())
                .username(user.getUsername())
                .active(user.isActive())
                .fullName(user.getPerson().getFullName())
                .departmentName(user.getDepartment().getName())
                .createdAt(LocalDateTime.ofInstant(user.getCreatedAt(), ZoneOffset.UTC))
                .updatedAt(LocalDateTime.ofInstant(user.getUpdatedAt(), ZoneOffset.UTC))
                .build();
    }
}

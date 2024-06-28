package com.challenge.manageruser.service.user;

import com.challenge.manageruser.model.dto.FilterDTO;
import com.challenge.manageruser.model.dto.user.SimpleUserDTO;
import com.challenge.manageruser.repository.UserRepository;
import com.challenge.manageruser.support.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class FindUserService {

    private final UserRepository userRepository;

    public FindUserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<SimpleUserDTO> getAllByFilter(final FilterDTO filter) {
        final var pageable = PageRequest.of(filter.page(), filter.size());
        return userRepository.findAll(pageable)
                .map(UserMapper::toSimpleUser);
    }
}

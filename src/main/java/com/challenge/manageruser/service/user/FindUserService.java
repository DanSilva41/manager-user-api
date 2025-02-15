package com.challenge.manageruser.service.user;

import com.challenge.manageruser.exception.InvalidUserException;
import com.challenge.manageruser.exception.NotFoundUserException;
import com.challenge.manageruser.model.dto.FilterDTO;
import com.challenge.manageruser.model.dto.user.DetailUserDTO;
import com.challenge.manageruser.model.dto.user.SimpleUserDTO;
import com.challenge.manageruser.model.entity.security.User;
import com.challenge.manageruser.repository.UserRepository;
import com.challenge.manageruser.support.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class FindUserService {

    private static final Logger log = LoggerFactory.getLogger(FindUserService.class);

    private final UserRepository userRepository;

    public FindUserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<SimpleUserDTO> getAllByFilter(final FilterDTO filter) {
        final var pageable = PageRequest.of(filter.page(), filter.size());
        return userRepository.findAll(pageable)
                .map(UserMapper::toSimpleUser);
    }

    public DetailUserDTO getDetailByCode(final Integer code) {
        return UserMapper.toDetailUser(this.getByCode(code));
    }

    public User getByCode(final Integer code) {
        log.info("m=searching user by code, userCode={}", code);
        return userRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundUserException("User with identifier %d not found".formatted(code)));
    }

    public void validateAlreadyExists(final String username, final String email) {
        log.info("m=validating if user already exists, username={}, email={}", username, email);
        if (userRepository.existsByUsernameOrPersonEmail(username, email)) {
            throw new InvalidUserException("There's already a user with this username or person email");
        }
    }

    public void validateAlreadyExists(final String username, final String email, final Integer userCodeForIgnore) {
        log.info("m=validating if user already exists ignoring user code, username={}, email={}, userCodeForIgnore={}", username, email, userCodeForIgnore);
        if (userRepository.existsByUsernameOrPersonEmailAndCodeNot(username, email, userCodeForIgnore)) {
            throw new InvalidUserException("There's already a user with this username or person email");
        }
    }

    public void validateIfNotExists(final Integer code) {
        log.info("m=validating if there is no user for code, userCode={}", code);
        if (!userRepository.existsByCode(code)) {
            throw new NotFoundUserException("User with identifier %d not found".formatted(code));
        }
    }
}

package com.challenge.manageruser.repository;

import com.challenge.manageruser.model.entity.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends org.springframework.data.repository.Repository<User, Integer> {

    @Transactional
    User save(User user);

    @Transactional(readOnly = true)
    boolean existsByUsernameOrPersonEmail(String username, String email);

    @Transactional(readOnly = true)
    Page<User> findAll(Pageable pageable);

    @Transactional(readOnly = true)
    Optional<User> findByCode(Integer code);
}

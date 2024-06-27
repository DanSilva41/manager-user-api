package com.challenge.manageruser.repository;

import com.challenge.manageruser.model.entity.security.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends org.springframework.data.repository.Repository<User, Integer> {

    @Transactional
    User save(User user);

    @Transactional(readOnly = true)
    boolean existsByPersonEmail(String email);
}

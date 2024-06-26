package com.challenge.manageruser.repository;

import com.challenge.manageruser.model.entity.security.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends org.springframework.data.repository.Repository<User, Integer> {

    @Transactional
    User save(User user);
}

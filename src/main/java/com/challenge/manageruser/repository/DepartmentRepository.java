package com.challenge.manageruser.repository;

import com.challenge.manageruser.model.entity.backing.Department;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends org.springframework.data.repository.Repository<Department, String> {

    @Transactional
    Department save(Department department);

    @Transactional(readOnly = true)
    boolean existsByName(String name);

    @Transactional(readOnly = true)
    Optional<Department> findByName(String name);
}

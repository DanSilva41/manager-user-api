package com.challenge.manageruser.support.builder.entity;

import com.challenge.manageruser.model.entity.backing.Department;
import com.challenge.manageruser.model.entity.backing.Person;
import com.challenge.manageruser.model.entity.security.User;

public class UserBuilder {

    private String username;
    private String password;
    private Boolean active;
    private Person person;
    private Department department;

    public User build() {
        return new User(username, password, active, person, department);
    }

    public UserBuilder username(final String username) {
        this.username = username;
        return this;
    }

    public UserBuilder password(final String password) {
        this.password = password;
        return this;
    }

    public UserBuilder active(final boolean active) {
        this.active = active;
        return this;
    }

    public UserBuilder person(final Person person) {
        this.person = person;
        return this;
    }

    public UserBuilder department(final Department department) {
        this.department = department;
        return this;
    }
}

package com.challenge.manageruser.support.builder.model;

import com.challenge.manageruser.model.backing.Person;
import com.challenge.manageruser.model.security.User;

public class UserBuilder {

    private Integer code;
    private String username;
    private String password;
    private boolean active;
    private Person person;

    public User build() {
        return new User(code, username, password, active, person);
    }

    public UserBuilder code(Integer code) {
        this.code = code;
        return this;
    }

    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder active(boolean active) {
        this.active = active;
        return this;
    }

    public UserBuilder person(Person person) {
        this.person = person;
        return this;
    }
}

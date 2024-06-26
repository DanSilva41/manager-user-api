package com.challenge.manageruser.support.builder.model;

import com.challenge.manageruser.model.backing.Person;

public class PersonBuilder {

    private Integer code;
    private String firstName;
    private String lastName;
    private String email;

    public Person build() {
        return new Person(code, firstName, lastName, email);
    }

    public PersonBuilder code(Integer code) {
        this.code = code;
        return this;
    }

    public PersonBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public PersonBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public PersonBuilder email(String email) {
        this.email = email;
        return this;
    }
}

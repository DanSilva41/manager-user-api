package com.challenge.manageruser.support.builder.entity;

import com.challenge.manageruser.model.entity.backing.Person;

public class PersonBuilder {

    private String firstName;
    private String lastName;
    private String email;

    public Person build() {
        return new Person(firstName, lastName, email);
    }

    public PersonBuilder firstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }

    public PersonBuilder lastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }

    public PersonBuilder email(final String email) {
        this.email = email;
        return this;
    }
}

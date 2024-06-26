package com.challenge.manageruser.model.backing;

import com.challenge.manageruser.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity(name = "backing.Person")
@Table(schema = "backing", name = "person")
public class Person extends BaseEntity {

    @Id
    @SequenceGenerator(schema = "backing", name = "person_code_seq", sequenceName = "person_code_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_code_seq")
    @Column(name = "code", unique = true, nullable = false)
    private Integer code;

    @NotBlank
    @Size(min = 2, max = 60)
    @Column(name = "first_name", length = 60, nullable = false)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 60)
    @Column(name = "last_name", length = 60, nullable = false)
    private String lastName;

    @Email
    @NotBlank
    @Size(min = 5, max = 100)
    @Column(name = "email", length = 150, unique = true, nullable = false)
    private String email;

    public Person() {
        // default constructor
    }

    public Person(final Integer code, final String firstName, final String lastName, final String email) {
        this.code = code;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Person person = (Person) o;
        return Objects.equals(code, person.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Person{" +
                "code=" + code +
                ", firstName='" + firstName +
                ", lastName='" + lastName +
                ", email=" + email +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}

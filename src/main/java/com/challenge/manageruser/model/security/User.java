package com.challenge.manageruser.model.security;

import com.challenge.manageruser.model.BaseEntity;
import com.challenge.manageruser.model.backing.Person;
import com.challenge.manageruser.support.builder.model.UserBuilder;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity(name = "security.User")
@Table(schema = "security", name = "user")
public class User extends BaseEntity {

    @Id
    @SequenceGenerator(schema = "security", name = "user_code_seq", sequenceName = "user_code_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_code_seq")
    @Column(name = "code", unique = true, nullable = false)
    private Integer code;

    @NotBlank
    @Size(min = 5, max = 20)
    @Column(name = "username", length = 20, unique = true, nullable = false)
    private String username;

    @NotBlank
    @Size(min = 6, max = 60)
    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @NotNull
    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // TODO avaliar CascadeType.ALL
    @JoinColumn(name = "person_code", nullable = false, unique = true)
    private Person person;

    public User() {
        // default constructor
    }

    public User(final Integer code, final String username, final String password, final boolean active, final Person person) {
        this.code = code;
        this.username = username;
        this.password = password;
        this.active = active;
        this.person = person;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(code, user.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "User{" +
                "code=" + code +
                ", username='" + username +
                ", password='" + password +
                ", active=" + active +
                ", person=" + person +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public Person getPerson() {
        return person;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }
}

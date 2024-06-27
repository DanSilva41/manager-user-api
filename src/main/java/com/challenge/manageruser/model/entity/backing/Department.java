package com.challenge.manageruser.model.entity.backing;

import com.challenge.manageruser.model.entity.BaseEntity;
import com.challenge.manageruser.support.builder.entity.DepartmentBuilder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity(name = "backing.Department")
@Table(schema = "backing", name = "department")
public class Department extends BaseEntity {

    @Id
    @NotBlank
    @Size(min = 2, max = 60)
    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @NotBlank
    @Size(min = 5, max = 100)
    @Column(name = "description", length = 60, nullable = false)
    private String description;

    public Department() {
        // default constructor
    }

    public Department(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Department person = (Department) o;
        return Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Person{" +
                ", name='" + name +
                ", description='" + description +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public static DepartmentBuilder builder() {
        return new DepartmentBuilder();
    }

}

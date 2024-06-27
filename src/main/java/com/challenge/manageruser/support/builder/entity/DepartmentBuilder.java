package com.challenge.manageruser.support.builder.entity;

import com.challenge.manageruser.model.entity.backing.Department;

public class DepartmentBuilder {

    private String name;
    private String description;

    public Department build() {
        return new Department(name, description);
    }

    public DepartmentBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public DepartmentBuilder description(final String description) {
        this.description = description;
        return this;
    }
}

package com.jorchdev.todo_api.dto.request;

import com.jorchdev.todo_api.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTaskRequest {
    @NotBlank
    public String name;

    @NotBlank
    public String description;

    @NotNull
    public User ownership;

    public CreateTaskRequest(){}

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnership(User ownership) {
        this.ownership = ownership;
    }
}

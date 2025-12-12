package com.jorchdev.todo_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateTaskRequest {
    @NotBlank
    public String name;

    @NotBlank
    public String description;

    public CreateTaskRequest(){}

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

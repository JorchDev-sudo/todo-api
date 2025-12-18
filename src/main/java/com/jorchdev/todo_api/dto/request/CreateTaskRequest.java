package com.jorchdev.todo_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTaskRequest {
    @NotBlank(message = "Task name is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    public String name;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    public String description;

    public CreateTaskRequest(){}

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

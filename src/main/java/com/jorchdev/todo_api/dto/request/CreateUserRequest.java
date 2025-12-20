package com.jorchdev.todo_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateUserRequest {

    @NotBlank(message = "User name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    public String name;

    public CreateUserRequest(){}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

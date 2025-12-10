package com.jorchdev.todo_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateUserRequest {

    @NotBlank
    public String name;

    public CreateUserRequest(){}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

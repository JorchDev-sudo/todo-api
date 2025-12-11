package com.jorchdev.todo_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserRequest {
    @NotBlank
    public String name;

    public UpdateUserRequest(){}
    public void setName(String name) {this.name = name;}
}

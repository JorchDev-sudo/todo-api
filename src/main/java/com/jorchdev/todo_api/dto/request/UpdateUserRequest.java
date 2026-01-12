package com.jorchdev.todo_api.dto.request;

import com.jorchdev.todo_api.entities.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {
    public String name;

    @Email
    public String email;

    @Size(min = 6, max = 32)
    public String password;

    public Roles role;

    public UpdateUserRequest(){}
    public void setName(String name) {this.name = name;}

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Roles role) {
        this.role = role;
    }
}

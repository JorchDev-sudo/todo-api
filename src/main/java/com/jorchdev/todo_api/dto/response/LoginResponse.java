package com.jorchdev.todo_api.dto.response;

public record LoginResponse(
        String token,
        String email,
        String name
) {}
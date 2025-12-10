package com.jorchdev.todo_api.dto.response;

import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.entities.enums.Status;

import java.time.LocalDateTime;

public record TaskResponse (Long id,
                            User ownership,
                            String name,
                            String description,
                            Status taskStatus,
                            LocalDateTime createdAt) {}

package com.jorchdev.todo_api.dto.response;

import com.jorchdev.todo_api.entities.enums.Status;

import java.time.LocalDateTime;

public record TaskResponse (Long id,
                            String name,
                            String description,
                            Status taskStatus,
                            LocalDateTime createdAt) {}

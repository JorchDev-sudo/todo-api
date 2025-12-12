package com.jorchdev.todo_api.dto.response;

import com.jorchdev.todo_api.entities.enums.Status;

import java.util.List;


public record UserResponse(Long id, String name, List<TaskResponse> userTasks){}

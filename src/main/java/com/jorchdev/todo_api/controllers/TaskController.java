package com.jorchdev.todo_api.controllers;

import com.jorchdev.todo_api.dto.request.CreateTaskRequest;
import com.jorchdev.todo_api.dto.request.UpdateTaskRequest;
import com.jorchdev.todo_api.dto.response.TaskResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.exceptions.ForbiddenException;
import com.jorchdev.todo_api.services.TaskService;
import com.jorchdev.todo_api.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final SecurityUtils securityUtils;

    public TaskController(TaskService taskService, SecurityUtils securityUtils)
    {
        this.taskService = taskService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        User user = securityUtils.getCurrentUser();
        if (user == null) {
            throw new ForbiddenException("Unauthorized");
        }


        TaskResponse response = taskService.createTask(user,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(defaultValue = "")String filterBy,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(defaultValue = "createdAt")String sortBy,
            @RequestParam(defaultValue = "desc")String direction){

        User user = securityUtils.getCurrentUser();
        if (user == null) {
            throw new ForbiddenException("Unauthorized");
        }


        return ResponseEntity.ok(taskService.findUserTasks(user.getId(), filterBy, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        User user = securityUtils.getCurrentUser();
        if (user == null) {
            throw new ForbiddenException("Unauthorized");
        }

        TaskResponse response = taskService.findTaskById(user.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        User user = securityUtils.getCurrentUser();
        if (user == null) {
            throw new ForbiddenException("Unauthorized");
        }


        TaskResponse response = taskService.updateTaskStatus(user.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        User user = securityUtils.getCurrentUser();
        if (user == null) {
            throw new ForbiddenException("Unauthorized");
        }


        taskService.deleteTask(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
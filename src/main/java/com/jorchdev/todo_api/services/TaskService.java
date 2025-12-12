package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.CreateTaskRequest;
import com.jorchdev.todo_api.dto.response.TaskResponse;
import com.jorchdev.todo_api.entities.Task;
import com.jorchdev.todo_api.mappers.TaskMapper;
import com.jorchdev.todo_api.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(UserService userService, TaskRepository taskRepository, TaskMapper taskMapper){
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userService = userService;
    }

    public List<TaskResponse> findUserTasks(Long id){
        return taskRepository.findTasksByUserId(id);
    }

    public TaskResponse createTask(Long id, CreateTaskRequest createTaskRequest){
        Task newTask = taskMapper.toEntity(userService.findUserByIdOrThrow(id), createTaskRequest);
        Task savedTask = taskRepository.save(newTask);


        return  taskMapper.toResponse(savedTask);
    }
}

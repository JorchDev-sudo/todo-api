package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.CreateTaskRequest;
import com.jorchdev.todo_api.dto.request.UpdateTaskRequest;
import com.jorchdev.todo_api.dto.response.TaskResponse;
import com.jorchdev.todo_api.entities.Task;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.mappers.TaskMapper;
import com.jorchdev.todo_api.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper){
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public List<TaskResponse> findUserTasks(Long id) {

        return taskRepository.findTasksByUserId(id)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    public TaskResponse createTask(User ownership, CreateTaskRequest createTaskRequest){
        Task newTask = taskMapper.toEntity(ownership, createTaskRequest);
        Task savedTask = taskRepository.save(newTask);

        return  taskMapper.toResponse(savedTask);
    }

    public TaskResponse updateTaskStatus(Long id, UpdateTaskRequest updateTaskRequest){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));


        taskMapper.toUpdateStatus(task, updateTaskRequest);

        taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    public void deleteTask(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        taskRepository.delete(task);
    }
}

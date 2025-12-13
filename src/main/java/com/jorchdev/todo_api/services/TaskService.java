package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.CreateTaskRequest;
import com.jorchdev.todo_api.dto.request.UpdateTaskRequest;
import com.jorchdev.todo_api.dto.response.TaskResponse;
import com.jorchdev.todo_api.entities.Task;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.mappers.TaskMapper;
import com.jorchdev.todo_api.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private static final Set<String> VALID_SORT_FIELDS = Set.of(
            "id",
            "name",
            "createdAt",
            "status");

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper){
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public Page<TaskResponse> findUserTasks(Long id,
                                            int page,
                                            int size,
                                            String sortBy,
                                            String direction) {

        if (!VALID_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> taskPage = taskRepository.findByUserId(id, pageable);

        return taskPage.map(taskMapper::toResponse);
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

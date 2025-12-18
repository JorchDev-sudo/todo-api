package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.CreateTaskRequest;
import com.jorchdev.todo_api.dto.request.UpdateTaskRequest;
import com.jorchdev.todo_api.dto.response.TaskResponse;
import com.jorchdev.todo_api.entities.Task;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.entities.enums.Status;
import com.jorchdev.todo_api.exceptions.ForbiddenException;
import com.jorchdev.todo_api.mappers.TaskMapper;
import com.jorchdev.todo_api.repositories.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private boolean isValidStatus(String value) {
        try {
            Status.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper){
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public TaskResponse findTaskById(Long userId, Long taskId) {
        Task task = taskRepository.findByIdAndOwnerShipId(userId, taskId)
                .orElseThrow(() -> new ForbiddenException(
                        "Task not found or you don't have permission"
                ));

        return taskMapper.toResponse(task);
    }

    public Page<TaskResponse> findUserTasks(
            Long userId,
            String filterBy,
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        if (!VALID_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }


        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> taskPage;

        if (filterBy == null || filterBy.isEmpty()) {
            taskPage = taskRepository.findByUserId(userId, pageable);

        } else if (isValidStatus(filterBy)) {
            Status status = Status.valueOf(filterBy.toUpperCase());
            taskPage = taskRepository.findByUserIdAndStatus(userId, status, pageable);

        } else if (filterBy.equalsIgnoreCase("createdToday")) {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
            taskPage = taskRepository.findByUserIdAndCreatedAtBetween(
                    userId, startOfDay, endOfDay, pageable
            );

        } else {
            throw new IllegalArgumentException("Invalid filter: " + filterBy);
        }

        return taskPage.map(taskMapper::toResponse);
    }

    public TaskResponse createTask(User ownership, CreateTaskRequest createTaskRequest){
        if (createTaskRequest.name == null || createTaskRequest.name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        Task newTask = taskMapper.toEntity(ownership, createTaskRequest);
        Task savedTask = taskRepository.save(newTask);

        return  taskMapper.toResponse(savedTask);
    }

    public TaskResponse updateTaskStatus(Long userId, Long taskId, UpdateTaskRequest updateTaskRequest){
        Task task = taskRepository.findByIdAndOwnerShipId(userId, taskId)
                .orElseThrow(() -> new ForbiddenException(
                        "Task not found or you don't have permission"
                ));

        taskMapper.toUpdateStatus(task, updateTaskRequest);
        taskRepository.save(task);

        return taskMapper.toResponse(task);
    }

    public void deleteTask(Long userId, Long taskId){
        Task task = taskRepository.findByIdAndOwnerShipId(userId, taskId)
                .orElseThrow(() -> new ForbiddenException(
                        "Task not found or you don't have permission"
                ));

        taskRepository.delete(task);
    }
}

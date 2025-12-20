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
import com.jorchdev.todo_api.repositories.UserRepository;
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
    private final UserRepository userRepository;
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

    public TaskService(TaskRepository taskRepository, UserRepository userRepository){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskResponse findTaskById(Long userId, Long taskId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ForbiddenException("User not found"));

        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new ForbiddenException(
                        "Task not found or you don't have permission"
                ));

        return TaskMapper.toResponse(task);
    }

    public Page<TaskResponse> findUserTasks(
            Long userId,
            String filterBy,
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ForbiddenException("User not found"));

        if (!VALID_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }


        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> taskPage;

        if (filterBy == null || filterBy.isEmpty()) {
            taskPage = taskRepository.findByUser(user, pageable);

        } else if (isValidStatus(filterBy)) {
            Status status = Status.valueOf(filterBy.toUpperCase());
            taskPage = taskRepository.findByUserAndStatus(user, status, pageable);

        } else if (filterBy.equalsIgnoreCase("createdToday")) {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
            taskPage = taskRepository.findByUserAndCreatedAtBetween(
                    user, startOfDay, endOfDay, pageable
            );

        } else {
            throw new IllegalArgumentException("Invalid filter: " + filterBy);
        }

        return taskPage.map(TaskMapper::toResponse);
    }

    public TaskResponse createTask(User ownership, CreateTaskRequest createTaskRequest){
        if (createTaskRequest.name == null || createTaskRequest.name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        Task newTask = TaskMapper.toEntity(ownership, createTaskRequest);
        Task savedTask = taskRepository.save(newTask);

        return  TaskMapper.toResponse(savedTask);
    }

    public TaskResponse updateTaskStatus(Long userId, Long taskId, UpdateTaskRequest updateTaskRequest){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ForbiddenException("User not found"));

        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new ForbiddenException(
                        "Task not found or you don't have permission"
                ));

        TaskMapper.toUpdateStatus(task, updateTaskRequest);
        taskRepository.save(task);

        return TaskMapper.toResponse(task);
    }

    public void deleteTask(Long userId, Long taskId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ForbiddenException("User not found"));

        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new ForbiddenException(
                        "Task not found or you don't have permission"
                ));

        taskRepository.delete(task);
    }
}

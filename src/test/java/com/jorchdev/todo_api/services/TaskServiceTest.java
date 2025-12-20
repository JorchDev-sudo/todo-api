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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    TaskService taskService;

    @Test
    void shouldReturnATask(){
        //Arrange
        User user = new User();

        Task task = new Task();
        task.setName("Jugar Lol");
        task.setUser(user);

        when(taskRepository.findByIdAndUser(task.getId(), user)).thenReturn(Optional.of(task));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //Act
        TaskResponse result = taskService.findTaskById(user.getId(), task.getId());

        //Assert
        assertThat(result.id()).isEqualTo(task.getId());

        verify(taskRepository).findByIdAndUser(task.getId(), user);

    }

    @Test
    void shouldReturnPagedUserTasks() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("PepeWithTasks");

        Task task1 = new Task();
        task1.setId(1L);
        task1.setUser(user);
        task1.setName("Task 1");
        task1.setStatus(Status.PENDING);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setUser(user);
        task2.setName("Task 2");
        task2.setStatus(Status.PENDING);

        Page<Task> taskPage = new PageImpl<>(List.of(task1, task2));


        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(taskRepository.findByUserAndStatus(
                eq(user),
                eq(Status.PENDING),
                any(Pageable.class))
        ).thenReturn(taskPage);

        // Act
        Page<TaskResponse> result = taskService.findUserTasks(
                user.getId(),
                "PENDING",
                0,
                10,
                "createdAt",
                "desc"
        );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).name()).isEqualTo("Task 1");
        assertThat(result.getContent().get(1).name()).isEqualTo("Task 2");

        verify(taskRepository).findByUserAndStatus(
                eq(user),
                eq(Status.PENDING),
                any(Pageable.class)
        );
    }


    @Test
    void shouldCreateANewTaskAndReturnAResponse() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("PepeWithTasks");

        CreateTaskRequest request = new CreateTaskRequest();
        request.setName("Dejar de jugar Lol");

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setUser(user);
        savedTask.setName("Dejar de jugar Lol");
        savedTask.setStatus(Status.PENDING);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskResponse result = taskService.createTask(user, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.taskStatus()).isEqualTo(Status.PENDING);

        verify(taskRepository).save(any(Task.class));
    }


    @Test
    void shouldUpdateStatusWhenUserOwnsTask() {
        // Arrange
        Long userId = 1L;
        Long taskId = 10L;

        User owner = new User();
        owner.setId(userId);

        Task task = new Task();
        task.setId(taskId);
        task.setUser(owner);
        task.setStatus(Status.PENDING);

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTaskStatus(Status.IN_PROGRESS);

        when(taskRepository.findByIdAndUser(taskId, owner))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(task)).thenReturn(task);
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        // Act
        TaskResponse result = taskService.updateTaskStatus(userId, taskId, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.taskStatus()).isEqualTo(Status.IN_PROGRESS);

        verify(taskRepository).findByIdAndUser(taskId, owner);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnTask() {
        // Arrange
        Long userId = 1L;
        Long taskId = 10L;

        User user = new User();
        user.setId(userId);

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTaskStatus(Status.DONE);

        when(userRepository.findById(eq(userId)))
                .thenReturn(Optional.of(user));

        when(taskRepository.findByIdAndUser(eq(taskId), eq(user)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                taskService.updateTaskStatus(userId, taskId, request)
        ).isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Task not found or you don't have permission");

        verify(taskRepository, never()).save(any(Task.class));
    }


    @Test
    void shouldDeleteTaskWhenUserIsTheOwner(){
        //Arrange
        User user = new User();

        Task task = new Task();
        task.setId(10L);
        task.setUser(user);

        when(taskRepository.findByIdAndUser(task.getId(), user)).thenReturn(Optional.of(task));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //Act
        taskService.deleteTask(user.getId(), task.getId());

        //Assert
        verify(taskRepository).findByIdAndUser(task.getId(), user);
        verify(taskRepository).delete(task);
    }

    @Test
    void shouldThrowExceptionWhenDeletingTaskUserDoesNotOwn() {
        // Arrange
        Long userId = 1L;
        Long taskId = 10L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(eq(userId)))
                .thenReturn(Optional.of(user));

        when(taskRepository.findByIdAndUser(eq(taskId), eq(user)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.deleteTask(userId, taskId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Task not found or you don't have permission");

        verify(taskRepository, never()).delete(any(Task.class));
    }
}

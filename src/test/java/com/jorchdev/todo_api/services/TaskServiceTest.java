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
    TaskMapper taskMapper;

    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    TaskService taskService;

    @Test
    void shouldReturnATask(){
        //Arrange
        Task task = new Task();
        task.setId(1L);

        Long taskId = 1L;
        Long userId = 10L;

        when(taskRepository.findByIdAndOwnerShipId(userId, taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(new TaskResponse(
                1L,
                null,
                null,
                null,
                null,
                null));
        //Act
        TaskResponse result = taskService.findTaskById(10L, 1L);

        //Assert
        assertThat(result.id()).isEqualTo(taskId);

        verify(taskRepository).findByIdAndOwnerShipId(userId, taskId);
        verify(taskMapper).toResponse(task);

    }

    @Test
    void shouldReturnPagedUserTasks(){
        //Arrange
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("PepeWithTasks");

        Task task1 = new Task();
        task1.setId(1L);
        task1.setOwnerShip(newUser);
        task1.setName("Task 1");

        Task task2 = new Task();
        task2.setId(2L);
        task2.setName("Task 2");

        TaskResponse taskResponse1 = new TaskResponse(
                1L,
                newUser,
                "Task 1",
                null,
                Status.PENDING,
                null);

        TaskResponse taskResponse2 = new TaskResponse(
                2L,
                newUser,
                "Task 2",
                null,
                Status.PENDING,
                null);

        Page<Task> taskPage = new PageImpl<>(List.of(task1, task2));

        when(taskRepository.findByUserIdAndStatus(eq(newUser.getId()), any(Status.class),any(Pageable.class)))
                .thenReturn(taskPage);

        when(taskMapper.toResponse(task1)).thenReturn(taskResponse1);
        when(taskMapper.toResponse(task2)).thenReturn(taskResponse2);

        //Act
        Page<TaskResponse> result = taskService.findUserTasks(
                newUser.getId(),
                "PENDING",
                0,
                10,
                "createdAt",
                "desc");

        //Assert
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(taskRepository).findByUserIdAndStatus(eq(newUser.getId()), any(Status.class), any(Pageable.class));

    }

    @Test
    void shouldCreateANewTaskAndReturnAResponse(){
        //Arrange
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("PepeWithTasks");

        CreateTaskRequest createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setName("Dejar de jugar Lol");

        Task newTask = new Task();
        newTask.setId(1L);
        newTask.setOwnerShip(newUser);
        newTask.setName("Dejar de jugar Lol");
        newTask.setDescription(null);

        TaskResponse taskResponse = new TaskResponse(
                1L,
                newUser,
                "Dejar de jugar Lol",
                null,
                Status.PENDING,
                newTask.getCreatedAt());

        when(taskMapper.toEntity(newUser ,createTaskRequest)).thenReturn(newTask);
        when(taskMapper.toResponse(newTask)).thenReturn(taskResponse);
        when(taskRepository.save(newTask)).thenReturn(newTask);

        //Act
        TaskResponse result = taskService.createTask(newUser, createTaskRequest);

        //Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(newTask.getId());
        assertThat(result.taskStatus()).isEqualTo(Status.PENDING);
        assertThat(result.ownership()).isEqualTo(newUser);

        verify(taskMapper).toEntity(newUser, createTaskRequest);
        verify(taskMapper).toResponse(newTask);
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
        task.setOwnerShip(owner);
        task.setStatus(Status.PENDING);

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTaskStatus(Status.IN_PROGRESS);

        when(taskRepository.findByIdAndOwnerShipId(userId, taskId))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(new TaskResponse(
                10L,
                owner,
                null,
                null,
                Status.IN_PROGRESS,
                null));

        // Act
        TaskResponse result = taskService.updateTaskStatus(userId, taskId, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.taskStatus()).isEqualTo(Status.IN_PROGRESS);

        verify(taskRepository).findByIdAndOwnerShipId(userId, taskId);
    }
    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnTask() {
        // Arrange
        Long userId = 1L;
        Long taskId = 10L;
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTaskStatus(Status.DONE);

        when(taskRepository.findByIdAndOwnerShipId(userId, taskId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                taskService.updateTaskStatus(userId, taskId, request)
        ).isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("permission");

        verify(taskRepository).findByIdAndOwnerShipId(userId, taskId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldDeleteTaskWhenUserIsTheOwner(){
        //Arrange
        Long userId = 1L;
        Task task = new Task();
        task.setId(10L);

        when(taskRepository.findByIdAndOwnerShipId(userId, task.getId())).thenReturn(Optional.of(task));

        //Act
        taskService.deleteTask(userId, task.getId());

        //Assert
        verify(taskRepository).findByIdAndOwnerShipId(userId, task.getId());
        verify(taskRepository).delete(task);
    }

    @Test
    void shouldThrowExceptionWhenDeletingTaskUserDoesNotOwn() {
        // Arrange
        Long userId = 1L;
        Long taskId = 10L;

        when(taskRepository.findByIdAndOwnerShipId(userId, taskId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.deleteTask(userId, taskId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("permission");

        verify(taskRepository).findByIdAndOwnerShipId(userId, taskId);
        verify(taskRepository, never()).delete(any(Task.class));
    }



}

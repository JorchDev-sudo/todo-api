package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.CreateTaskRequest;
import com.jorchdev.todo_api.dto.request.UpdateTaskRequest;
import com.jorchdev.todo_api.dto.response.TaskResponse;
import com.jorchdev.todo_api.entities.Task;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.entities.enums.Status;
import com.jorchdev.todo_api.mappers.TaskMapper;
import com.jorchdev.todo_api.repositories.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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
    void shouldReturnUserTasks(){
        //Arrange
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("PepeWithTasks");

        Task newTask = new Task();
        newTask.setId(1L);
        newTask.setOwnerShip(newUser);
        newTask.setName("Jugar Lol");

        List<Task> userTasks = new ArrayList<>();
        userTasks.add(newTask);

        when(taskRepository.findTasksByUserId(newUser.getId())).thenReturn(userTasks);
        when(taskMapper.toResponse(newTask)).thenReturn(
                new TaskResponse(
                        1L,
                        newUser,
                        "Jugar Lol",
                        null,
                        Status.IN_PROGRESS,
                        null));

        //Act
        List<TaskResponse> resultTasks = taskService.findUserTasks(newUser.getId());

        //Assert
        assertThat(resultTasks).isNotNull();
        assertThat(resultTasks).hasAtLeastOneElementOfType(TaskResponse.class);
        assertThat(resultTasks.get(0).name()).isEqualTo(newTask.getName());
        assertThat(resultTasks.get(0).id()).isEqualTo(newTask.getId());
        assertThat(resultTasks.get(0).ownership()).isEqualTo(newUser);
        assertThat(resultTasks.get(0).taskStatus()).isEqualTo(Status.IN_PROGRESS);

        verify(taskRepository.findTasksByUserId(newUser.getId()));
        verify(taskMapper.toResponse(newTask));
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
    void shouldUpdateTaskStatus(){
        //Arrange
        Task task = new Task();
        task.setId(1L);
        task.setName("Jugar Lol");

        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest();
        updateTaskRequest.setTaskStatus(Status.IN_PROGRESS);


        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskMapper.toUpdateStatus(task, updateTaskRequest)).thenReturn(task);
        when(taskRepository.save(any())).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(new TaskResponse(
                1L,
                null,
                "Jugar Lol",
                null,
                Status.IN_PROGRESS,
                null
        ));

        //Act
        TaskResponse updatedTask = taskService.updateTaskStatus(task.getId(), updateTaskRequest);

        //Assert
        assertThat(updatedTask.id()).isEqualTo(task.getId());
        assertThat(updatedTask.ownership()).isEqualTo(task.getOwnerShip());
        assertThat(updatedTask.taskStatus()).isEqualTo(Status.IN_PROGRESS);

        verify(taskRepository).findById(task.getId());
        verify(taskRepository).save(task);
    }

    @Test
    void shouldDeleteTask(){
        //Arrange
        Task task = new Task();
        task.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        //Act
        taskService.deleteTask(task.getId());

        //Assert
        verify(taskRepository).findById(task.getId());
        verify(taskRepository).delete(task);
    }



}

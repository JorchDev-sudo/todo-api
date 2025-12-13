package com.jorchdev.todo_api.mappers;


import com.jorchdev.todo_api.dto.request.CreateTaskRequest;
import com.jorchdev.todo_api.dto.request.UpdateTaskRequest;
import com.jorchdev.todo_api.dto.response.TaskResponse;
import com.jorchdev.todo_api.entities.Task;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.entities.enums.Status;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

public class TaskMapperTest {
    public TaskMapper taskMapper = new TaskMapper();

    @Test
    public void givenValidRequest_whenMappingToCreateEntity_thenTaskIsCreatedWithCorrectData(){
        User ownership = new User();
        ownership.setId(1L);

        CreateTaskRequest createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setName("Jugar Lol");
        createTaskRequest.setDescription("No jueguen Lol");

        Task result = taskMapper.toEntity(ownership, createTaskRequest);

        assertThat(result.getOwnerShip()).isEqualTo(ownership);

    }

    @Test
    public void givenValidEntity_whenMappingToResponse_thenResponseIsCreatedWithCorrectData(){
        Task newTask = new Task();
        newTask.setName("Dejar de jugar lol");

        TaskResponse result = taskMapper.toResponse(newTask);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(newTask.getName());
    }

    @Test
    public void givenValidRequest_whenMappingToEntity_thenStatusIsChanged(){
        Task newTask = new Task();
        newTask.setId(1L);
        newTask.setName("Jugar Lol");

        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest();
        updateTaskRequest.setTaskStatus(Status.IN_PROGRESS);

        Task result = taskMapper.toUpdateStatus(newTask ,updateTaskRequest);

        assertThat(result.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }
}

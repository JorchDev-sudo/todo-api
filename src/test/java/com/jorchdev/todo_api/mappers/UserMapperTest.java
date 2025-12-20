package com.jorchdev.todo_api.mappers;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;
import static org.assertj.core.api.Assertions.assertThat;

import com.jorchdev.todo_api.dto.request.UpdateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.Task;
import com.jorchdev.todo_api.entities.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class UserMapperTest {
    UserMapper userMapper = new UserMapper();

    @Test
    public void givenValidRequest_whenMappingToCreateEntity_thenUserIsCreatedWithCorrectData(){
        CreateUserRequest newUserRequest = new CreateUserRequest();
        newUserRequest.setName("Pepe");

        User newUser = userMapper.toCreateEntity(newUserRequest);

        assertThat(newUser.getName()).isEqualTo("Pepe");
        assertThat(newUser.getId()).isNull();
        assertThat(newUser.getUserTasks()).isNull();
    }

    @Test
    public void givenValidEntity_whenMappingToResponse_thenResponseisCreatedWithCorrectData(){
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("Pepe");

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());

        newUser.setUserTasks(tasks);

        UserResponse result = userMapper.toResponse(newUser);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(newUser.getId());
        assertThat(result.name()).isEqualTo(newUser.getName());
    }

    @Test
    public void givenValidRequest_whenMappingToUpdateEntity_thenUserIsUpdatedWithCorrectData(){
        User user = new User();
        user.setId(1L);
        user.setName("Pepe");

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Pepito");

        userMapper.updateEntity(user, updateUserRequest, "");

        assertThat(user.getName()).isEqualTo("Pepito");
    }
}

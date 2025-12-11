package com.jorchdev.todo_api.mappers;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;
import static org.assertj.core.api.Assertions.assertThat;

import com.jorchdev.todo_api.entities.User;
import org.junit.jupiter.api.Test;

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
}

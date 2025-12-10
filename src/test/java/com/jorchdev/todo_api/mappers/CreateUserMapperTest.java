package com.jorchdev.todo_api.mappers;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;
import static org.assertj.core.api.Assertions.assertThat;

import com.jorchdev.todo_api.entities.User;
import org.junit.jupiter.api.Test;

public class CreateUserMapperTest {
    CreateUserMapper createUserMapper = new CreateUserMapper();

    @Test
    public void givenValidRequest_whenMappingToEntity_thenUserIsCreatedWithCorrectData(){
        CreateUserRequest newUserRequest = new CreateUserRequest();
        newUserRequest.setName("Pepe");

        User newUser = createUserMapper.toEntity(newUserRequest);

        assertThat(newUser.getName()).isEqualTo("Pepe");
        assertThat(newUser.getId()).isNull();
        assertThat(newUser.getUserTasks()).isNull();
    }
}

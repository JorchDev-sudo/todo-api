package com.jorchdev.todo_api.mappers;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.User;
import org.springframework.stereotype.Component;

@Component
public class CreateUserMapper {
    public User toEntity(CreateUserRequest userRequest){
        User newUser = new User();
        newUser.setName(userRequest.name);

        return newUser;
    }

    public UserResponse toResponse(User user){
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getUserTasks());

        return userResponse;
    }
}

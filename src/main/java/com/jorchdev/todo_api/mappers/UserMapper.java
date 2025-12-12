package com.jorchdev.todo_api.mappers;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;
import com.jorchdev.todo_api.dto.request.UpdateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toCreateEntity(CreateUserRequest userRequest){
        User newUser = new User();
        newUser.setName(userRequest.name);

        return newUser;
    }

    public UserResponse toResponse(User user){

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getUserTasks());
    }

    public User toUpdateEntity(UpdateUserRequest updateUserRequest){
        User updatedUser = new User();
        updatedUser.setName(updateUserRequest.name);

        return updatedUser;
    }
}

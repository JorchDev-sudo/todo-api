package com.jorchdev.todo_api.mappers;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;
import com.jorchdev.todo_api.dto.request.UpdateUserRequest;
import com.jorchdev.todo_api.dto.response.TaskResponse;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.Task;
import com.jorchdev.todo_api.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Mapper(componentModel = "spring")
public class UserMapper {

    public User toCreateEntity(CreateUserRequest userRequest){
        User newUser = new User();
        newUser.setName(userRequest.name);

        return newUser;
    }

    public UserResponse toResponse(User user){
        List<TaskResponse> tasks = new ArrayList<>();

        for (Task task : user.getUserTasks()) {
            TaskResponse response = TaskMapper.toResponse(task);
            tasks.add(response);
        }

        return new UserResponse(
                user.getId(),
                user.getName(),
                tasks);

    }

    public void updateEntity(@MappingTarget User user,
                             UpdateUserRequest request, String hashedPassword) {

        user.setName(request.name);
        user.setEmail(request.email);
        user.setPassword(hashedPassword);
    }

}

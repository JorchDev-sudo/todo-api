package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.mappers.CreateUserMapper;
import com.jorchdev.todo_api.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CreateUserMapper createUserMapper;

    public UserService(UserRepository userRepository, CreateUserMapper createUserMapper){
        this.userRepository = userRepository;
        this.createUserMapper = createUserMapper;
    }

    public UserResponse createUser(CreateUserRequest userRequest){
        User newUser = createUserMapper.toEntity(userRequest);
        User savedUser = userRepository.save(newUser);

        return createUserMapper.toResponse(savedUser);
    }

}

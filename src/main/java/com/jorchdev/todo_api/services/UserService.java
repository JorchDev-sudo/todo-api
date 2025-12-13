package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;
import com.jorchdev.todo_api.dto.request.UpdateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.mappers.UserMapper;
import com.jorchdev.todo_api.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse createUser(CreateUserRequest userRequest){
        User newUser = userMapper.toCreateEntity(userRequest);
        User savedUser = userRepository.save(newUser);

        return userMapper.toResponse(savedUser);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) {
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        User user;
        user = userRepository.save(userMapper.toUpdateEntity(updateUserRequest));

        return userMapper.toResponse(user);
    }

    public void deleteUser(Long id){
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepository.deleteById(id);
    }

    protected void deleteAllUsers(){
        userRepository.deleteAll();
    }

}

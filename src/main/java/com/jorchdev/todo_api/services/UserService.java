package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;
import com.jorchdev.todo_api.dto.request.UpdateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.mappers.UserMapper;
import com.jorchdev.todo_api.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final Set<String> VALID_SORT_FIELDS = Set.of("id", "name");

    public UserService(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public Page<UserResponse> findPagedUsers(int page, int size, String sortBy, String direction) {
        if (!VALID_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> usersPage = userRepository.findAll(pageable);

        return usersPage.map(userMapper::toResponse);
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

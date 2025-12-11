package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;

import com.jorchdev.todo_api.dto.request.UpdateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.mappers.UserMapper;
import com.jorchdev.todo_api.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldFindAllUsers(){
        //Arrange
        User user1 = new User();
        user1.setName("Pepe");
        User user2 = new User();
        user2.setName("Pipi");
        User user3 = new User();
        user3.setName("Popo");

        UserResponse userResponse1 = new UserResponse(1L, "Pepe", null);
        UserResponse userResponse2 = new UserResponse(2L, "Pipi", null);
        UserResponse userResponse3 = new UserResponse(3L, "Popo", null);

        List<User> allUsers = List.of(user1, user2, user3);

        when(userRepository.findAll()).thenReturn(allUsers);
        when(userMapper.toResponse(user1)).thenReturn(userResponse1);
        when(userMapper.toResponse(user2)).thenReturn(userResponse2);
        when(userMapper.toResponse(user3)).thenReturn(userResponse3);


        //Act
        List<UserResponse> result = userService.findAllUsers();

        //Assert
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(userResponse1);
        assertThat(result.get(1)).isEqualTo(userResponse2);
        assertThat(result.get(2)).isEqualTo(userResponse3);

        verify(userRepository).findAll();
        verify(userMapper, times(3)).toResponse(any(User.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        //Arrange
        List<User> userList = new ArrayList<>();

        when(userRepository.findAll()).thenReturn(userList);

        //Act
        List<UserResponse> result = userService.findAllUsers();

        //Assert
        assertThat(result).isEmpty();

        verify(userRepository).findAll();
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void shouldCreateANewUserAndReturnAResponse() {

        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Pepe");

        User mappedUser = new User();
        mappedUser.setName("Pepe");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Pepe");

        when(userMapper.toCreateEntity(request)).thenReturn(mappedUser);
        when(userRepository.save(mappedUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(new UserResponse(1L,"Pepe", null));

        // Act
        UserResponse response = userService.createUser(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Pepe");

        // Verify
        verify(userMapper).toCreateEntity(request);
        verify(userMapper).toResponse(savedUser);
        verify(userRepository).save(mappedUser);
    }

    @Test
    void shouldUpdateUserAndReturnAResponse(){
        //Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Pepe");

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Pepito");

        UserResponse userResponse = new UserResponse(1L, "Pepito", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        //Act
        UserResponse result = userService.updateUser(1L, updateUserRequest);

        //Assert
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Pepito");

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);

    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        Long userId = 999L;
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Pepito");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        assertThatThrownBy(() -> userService.updateUser(userId, updateUserRequest))
                .isInstanceOf(EntityNotFoundException.class);

        //Assert
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }
}
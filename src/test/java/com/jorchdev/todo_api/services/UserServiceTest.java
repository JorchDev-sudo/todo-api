package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;

import com.jorchdev.todo_api.dto.request.UpdateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.exceptions.ForbiddenException;
import com.jorchdev.todo_api.mappers.UserMapper;
import com.jorchdev.todo_api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

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
    void shouldReturnUser(){
        //Arrange
        User user = new User();
        user.setId(1L);

        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(new UserResponse(1L, null, null));
        //Act
        UserResponse result = userService.findUser(id);

        //Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(user.getId());
    }

    @Test
    void shouldFindAllPagedUsers(){
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
        Page<User> userPage = new PageImpl<>(allUsers);

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(userPage);

        when(userMapper.toResponse(user1)).thenReturn(userResponse1);
        when(userMapper.toResponse(user2)).thenReturn(userResponse2);
        when(userMapper.toResponse(user3)).thenReturn(userResponse3);

        //Act
        Page<UserResponse> result = userService.findPagedUsers(
                0,
                10,
                "id",
                "desc");

        //Assert
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.isFirst()).isTrue();

        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        //Arrange
        List<User> userList = new ArrayList<>();
        Page<User> userPage = new PageImpl<>(userList);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        //Act
        Page<UserResponse> result = userService.findPagedUsers(1, 1, "id", "desc");

        //Assert
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);

        verify(userRepository).findAll(any(Pageable.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    //Remember to add to TaskServiceTest
    @Test
    void shouldThrowExceptionWhenSortFieldIsInvalid() {
        // Arrange
        String invalidSortField = "invalidField";

        // Act & Assert
        assertThatThrownBy(() ->
                userService.findPagedUsers(0, 10, invalidSortField, "asc")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid sort field");

        verify(userRepository, never()).findAll(any(Pageable.class));
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
        Long principalId = 1L;

        User user = new User();
        user.setId(1L);
        user.setName("Pepe");

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Pepito");

        UserResponse userResponse = new UserResponse(1L, "Pepito", null);

        when(userRepository.findById(user.getId(), principalId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        //Act
        UserResponse result = userService.updateUser(user.getId(), principalId , updateUserRequest);

        //Assert
        assertThat(result.id()).isEqualTo(principalId);
        assertThat(result.name()).isEqualTo(user.getName());

        verify(userRepository).findById(user.getId(), principalId);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);

    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOrPrincipalDoesNotOwn() {
        // Arrange
        Long principalId = 1L;
        Long userId = 10L;
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();

        when(userRepository.findById(userId, principalId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> userService.updateUser(userId, principalId, updateUserRequest))
                .isInstanceOf(ForbiddenException.class)
                        .hasMessageContaining("permission");

        verify(userRepository).findById(userId);
    }

    @Test
    void shouldDeleteUser(){
        //Arrange
        Long principalId = 1L;

        User userToDelete = new User();
        userToDelete.setId(1L);
        userToDelete.setName("Pepe");

        when(userRepository.findById(1L, principalId)).thenReturn(Optional.of(userToDelete));

        //Act
        userService.deleteUser(userToDelete.getId(), principalId);

        //Assert
        verify(userRepository).findById(userToDelete.getId(), principalId);
        verify(userRepository).deleteById(userToDelete.getId());
    }
    @Test
    void shouldThrowExceptionWhenUserNotFoundForDeleteOrPrincipalDoesNotOwn() {
        // Arrange
        Long principalId = 1L;
        Long userId = 10L;

        when(userRepository.findById(userId, principalId)).thenReturn(Optional.empty());

        // Act
        assertThatThrownBy(() -> userService.deleteUser(userId, principalId))
                .isInstanceOf(ForbiddenException.class)
                        .hasMessageContaining("permission");

        //Assert
        verify(userRepository).findById(userId, principalId);
    }
}
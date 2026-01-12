package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.DeleteAccountRequest;
import com.jorchdev.todo_api.dto.request.UpdateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.mappers.UserMapper;
import com.jorchdev.todo_api.repositories.UserRepository;
import com.jorchdev.todo_api.utils.DeleteAccountConstants;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUser(){
        User user = new User();
        user.setId(1L);

        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(new UserResponse(1L, null, null));
        UserResponse result = userService.findUser(id);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(user.getId());
    }

    @Test
    void shouldFindAllPagedUsers(){
        User user1 = new User();
        user1.setName("Example1");
        User user2 = new User();
        user2.setName("Example2");
        User user3 = new User();
        user3.setName("Example3");

        UserResponse userResponse1 = new UserResponse(1L, "Example1", null);
        UserResponse userResponse2 = new UserResponse(2L, "Example2", null);
        UserResponse userResponse3 = new UserResponse(3L, "Example3", null);

        List<User> allUsers = List.of(user1, user2, user3);
        Page<User> userPage = new PageImpl<>(allUsers);

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(userPage);

        when(userMapper.toResponse(user1)).thenReturn(userResponse1);
        when(userMapper.toResponse(user2)).thenReturn(userResponse2);
        when(userMapper.toResponse(user3)).thenReturn(userResponse3);

        Page<UserResponse> result = userService.findPagedUsers(
                0,
                10,
                "id",
                "desc");

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.isFirst()).isTrue();

        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        List<User> userList = new ArrayList<>();
        Page<User> userPage = new PageImpl<>(userList);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<UserResponse> result = userService.findPagedUsers(1, 1, "id", "desc");

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);

        verify(userRepository).findAll(any(Pageable.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenSortFieldIsInvalid() {
        String invalidSortField = "invalidField";

        assertThatThrownBy(() ->
                userService.findPagedUsers(0, 10, invalidSortField, "asc")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid sort field");

        verify(userRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void shouldUpdateUserAndReturnAResponse() {
        User user = new User();
        user.setId(1L);
        user.setName("OldExample");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("NewExample");

        UserResponse response = new UserResponse(1L, "NewExample", null);

        when(userRepository.findByEmail(null)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        when(passwordEncoder.encode(request.password)).thenReturn("hashedPassword");
        when(userMapper.toResponse(any(User.class))).thenReturn(response);

        UserResponse result = userService.updateUser(user, request);

        assertThat(result.name()).isEqualTo("NewExample");

        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOrPrincipalDoesNotOwn() {
        User user = new User();
        user.setId(10L);
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(user, updateUserRequest))
                .isInstanceOf(EntityNotFoundException.class)
                        .hasMessageContaining("User not found");

        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void shouldDeleteUser() {
        User userToDelete = new User();
        userToDelete.setId(1L);
        userToDelete.setName("Example");

        DeleteAccountRequest request = new DeleteAccountRequest();
        request.setConfirmation(DeleteAccountConstants.CONFIRMATION_PHRASE);

        userService.deleteCurrentUser(request, userToDelete);

        verify(userRepository).delete(userToDelete);
    }
}
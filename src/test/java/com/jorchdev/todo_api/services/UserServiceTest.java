package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;

import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.mappers.CreateUserMapper;
import com.jorchdev.todo_api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private CreateUserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

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

        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(userRepository.save(mappedUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(new UserResponse(1L,"Pepe", null));

        // Act
        UserResponse response = userService.createUser(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Pepe");

        // Verify
        verify(userMapper).toEntity(request);
        verify(userMapper).toResponse(savedUser);
        verify(userRepository).save(mappedUser);
    }
}
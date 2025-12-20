/*
package com.jorchdev.todo_api.controllers;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.get;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        // Arrange: Mockeas el Service
        when(userService.findUser(999L))
                .thenThrow(new EntityNotFoundException("User not found"));

        // Act: Simulas un HTTP GET request
        mockMvc.perform((RequestBuilder) get("/api/users/999"))
                // Assert: Verificas la respuesta HTTP
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        // ARRANGE
        CreateUserRequest request = new CreateUserRequest();
        request.setName("John Doe");

        UserResponse response = new UserResponse(1L, "John Doe", null);

        when(userService.createUser(any(CreateUserRequest.class)))
                .thenReturn(response);

        // ACT & ASSERT
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "name": "John Doe"
                    }
                    """))
                .andExpect(status().isCreated())  // 201
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        // Verificar que el Service fue llamado
        verify(userService).createUser(any(CreateUserRequest.class));
    }
}

 */
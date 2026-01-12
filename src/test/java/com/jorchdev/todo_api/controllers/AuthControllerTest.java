package com.jorchdev.todo_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jorchdev.todo_api.dto.request.LoginRequest;
import com.jorchdev.todo_api.dto.request.RegisterRequest;
import com.jorchdev.todo_api.dto.response.LoginResponse;
import com.jorchdev.todo_api.dto.response.RegisterResponse;
import com.jorchdev.todo_api.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    @Test
    void register_shouldReturn201_whenSuccessful() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("example");
        request.setEmail("example@email.com");
        request.setPassword("password");

        RegisterResponse response = new RegisterResponse(
                "User created successfully",
                request.getEmail(),
                request.getName());

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("example@email.com"))
                .andExpect(jsonPath("$.name").value("example"));
    }

    @Test
    void register_shouldReturn400_whenServiceThrowsException() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("example");
        request.setEmail("example@email.com");
        request.setPassword("password");

        when(authService.register(any()))
                .thenThrow(new IllegalStateException("Email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturn200_whenSuccessful() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("example@email.com");
        request.setPassword("password");

        LoginResponse response = new LoginResponse(
                "jwt-token",
                request.getEmail(),
                "example");

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_shouldReturn401_whenServiceThrowsException() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("example@email.com");
        request.setPassword("wrong-password");

        when(authService.login(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test_shouldReturn200_andMessage() throws Exception {
        mockMvc.perform(get("/api/auth/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Auth working correctly"));
    }
}

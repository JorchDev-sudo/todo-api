package com.jorchdev.todo_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jorchdev.todo_api.dto.request.DeleteAccountRequest;
import com.jorchdev.todo_api.dto.request.UpdateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.entities.enums.Roles;
import com.jorchdev.todo_api.services.UserService;
import com.jorchdev.todo_api.utils.DeleteAccountConstants;
import com.jorchdev.todo_api.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }

    // =========================
    // GET ALL USERS
    // =========================

    @Test
    void getAllUsers_shouldReturnOkAndInvokeService() throws Exception {
        Page<UserResponse> page =
                new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(userService.findPagedUsers(0, 10, "id", "asc"))
                .thenReturn(page);

        mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk());
        verify(userService).findPagedUsers(0, 10, "id", "asc");
    }



    // =========================
    // GET USER BY ID
    // =========================

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        UserResponse response = new UserResponse(1L, "example", null);

        when(userService.findUser(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("example"));
    }

    // =========================
    // UPDATE USER
    // =========================

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("NewExample");
        request.setRole(Roles.USER);
        request.setPassword("password");
        request.setEmail("example@email.com");

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setName("NewExample");
        currentUser.setPassword("password");
        currentUser.setRole(Roles.USER);
        currentUser.setEmail("example@email.com");

        UserResponse response = new UserResponse(1L, "NewExample", null);

        when(securityUtils.getCurrentUser())
                .thenReturn(currentUser);

        when(userService.updateUser(
                any(User.class),
                any(UpdateUserRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "NewExample"
                        }
                    """))
                .andExpect(status().isOk());

        verify(securityUtils).getCurrentUser();
        verify(userService).updateUser(any(User.class), any(UpdateUserRequest.class));
    }


    // =========================
    // DELETE MY ACCOUNT
    // =========================

    @Test
    void deleteMyAccount_shouldReturn204() throws Exception {
        DeleteAccountRequest request = new DeleteAccountRequest();
        request.setConfirmation(DeleteAccountConstants.CONFIRMATION_PHRASE);

        User user = new User();
        user.setName("example");

        when(securityUtils.getCurrentUser()).thenReturn(user);

        mockMvc.perform(delete("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isNoContent());
    }
}

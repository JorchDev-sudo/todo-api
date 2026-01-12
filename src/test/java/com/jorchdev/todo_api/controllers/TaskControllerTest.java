package com.jorchdev.todo_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jorchdev.todo_api.dto.request.CreateTaskRequest;
import com.jorchdev.todo_api.dto.request.UpdateTaskRequest;
import com.jorchdev.todo_api.dto.response.TaskResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.entities.enums.Status;
import com.jorchdev.todo_api.services.TaskService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private TaskController taskController;

    private ObjectMapper objectMapper;

    private User mockUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders
                .standaloneSetup(taskController)
                .build();

        mockUser = new User();
        mockUser.setId(1L);
    }

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        User user = new User();
        user.setId(1L);

        TaskResponse response = new TaskResponse(
                1L,
                "Task name",
                "Task description",
                Status.PENDING,
                LocalDateTime.now()
        );

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(taskService.createTask(any(User.class), any(CreateTaskRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Task name",
                          "description": "Task description"
                        }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Task name"))
                .andExpect(jsonPath("$.description").value("Task description"));

        verify(securityUtils).getCurrentUser();
        verify(taskService).createTask(any(User.class), any(CreateTaskRequest.class));
    }

    @Test
    void getAllTasks_shouldReturnPagedTasks() throws Exception {
        // given
        TaskResponse task = new TaskResponse(
                1L,
                "Test task",
                "Task description",
                Status.PENDING,
                LocalDateTime.now()
        );

        List<TaskResponse> content = new ArrayList<>();
        content.add(task);

        Page<TaskResponse> page = new PageImpl<>(
                content,
                PageRequest.of(0, 10),
                1
        );

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(taskService.findUserTasks(
                eq(1L),
                eq(""),
                eq(0),
                eq(10),
                eq("createdAt"),
                eq("desc")
        )).thenReturn(page);

        // when / then
        mockMvc.perform(get("/api/tasks")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Test task"))
                .andExpect(jsonPath("$.content[0].taskStatus").value("PENDING"));
    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        User user = new User();
        user.setId(1L);

        TaskResponse response = new TaskResponse(
                1L,
                "Task",
                "Desc",
                Status.PENDING,
                LocalDateTime.now()
        );

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(taskService.findTaskById(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Task"));

        verify(taskService).findTaskById(1L, 1L);
    }

    @Test
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        User user = new User();
        user.setId(1L);

        TaskResponse response = new TaskResponse(
                1L,
                "Task",
                "Desc",
                Status.DONE,
                LocalDateTime.now()
        );

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(taskService.updateTaskStatus(
                eq(1L),
                eq(1L),
                any(UpdateTaskRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "taskStatus": "DONE"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskStatus").value("DONE"));

        verify(taskService).updateTaskStatus(eq(1L), eq(1L), any());
    }

    @Test
    void deleteTask_shouldReturnNoContent() throws Exception {
        User user = new User();
        user.setId(1L);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        doNothing().when(taskService).deleteTask(1L, 1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L, 1L);
    }
}

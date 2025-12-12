package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.mappers.TaskMapper;
import com.jorchdev.todo_api.repositories.TaskRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    TaskMapper taskMapper;

    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    TaskService taskService;




}

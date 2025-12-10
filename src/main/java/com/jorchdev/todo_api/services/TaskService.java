package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.response.TaskResponse;
import com.jorchdev.todo_api.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> findUserTasks(Long id){
        return taskRepository.findTasksByUserId(id);
    }
}

package com.jorchdev.todo_api.mappers;

import com.jorchdev.todo_api.dto.request.CreateTaskRequest;
import com.jorchdev.todo_api.dto.response.TaskResponse;
import com.jorchdev.todo_api.entities.Task;
import com.jorchdev.todo_api.entities.User;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(User ownership, CreateTaskRequest taskRequest){
        Task newTask = new Task();
        newTask.setName(taskRequest.name);
        newTask.setDescription(taskRequest.description);
        newTask.setOwnerShip(ownership);

        return newTask;
    }

    public TaskResponse toResponse(Task task){
        return new TaskResponse(
                task.getId(),
                task.getOwnerShip(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt());
    }
}

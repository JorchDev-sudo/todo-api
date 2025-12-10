package com.jorchdev.todo_api.mappers;

import com.jorchdev.todo_api.dto.request.CreateTaskRequest;
import com.jorchdev.todo_api.entities.Task;
import org.springframework.stereotype.Component;

@Component
public class CreateTaskMapper {
    public Task toEntity(CreateTaskRequest taskRequest){
        Task newTask = new Task();
        newTask.setName(taskRequest.name);
        newTask.setDescription(taskRequest.description);
        newTask.setOwnerShip(taskRequest.ownership);
        return newTask;
    }
}

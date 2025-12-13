package com.jorchdev.todo_api.dto.request;

import com.jorchdev.todo_api.entities.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateTaskRequest {
    @NotNull
    public Status taskStatus;

    public UpdateTaskRequest() {}

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }


}

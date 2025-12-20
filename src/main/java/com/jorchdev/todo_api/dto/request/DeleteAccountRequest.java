package com.jorchdev.todo_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DeleteAccountRequest {

    @NotBlank
    private String confirmation;

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }
    public String getConfirmation() {
        return confirmation;
    }
}

package com.jorchdev.todo_api.controllers;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;
import com.jorchdev.todo_api.dto.request.DeleteAccountRequest;
import com.jorchdev.todo_api.dto.request.UpdateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.exceptions.ForbiddenException;
import com.jorchdev.todo_api.services.UserService;
import com.jorchdev.todo_api.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final SecurityUtils securityUtils;

    @Autowired
    public UserController(UserService userService, SecurityUtils securityUtils){
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction){

        return ResponseEntity.ok(userService.findPagedUsers(page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.findUser(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(
            @Valid @RequestBody UpdateUserRequest request
    ) {
        User user = securityUtils.getCurrentUser();
        if (user == null) {
            throw new ForbiddenException("Unauthorized");
        }

        UserResponse updated = userService.updateUser(user ,request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(
            @Valid @RequestBody DeleteAccountRequest request
    ) {
        User user = securityUtils.getCurrentUser();
        if (user == null) {
            throw new ForbiddenException("Unauthorized");
        }

        userService.deleteCurrentUser(request, user);

        return ResponseEntity.noContent().build();
    }


}

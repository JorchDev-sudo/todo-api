package com.jorchdev.todo_api.entities;

import com.jorchdev.todo_api.dto.response.TaskResponse;
import com.jorchdev.todo_api.entities.enums.Roles;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Roles role = Roles.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskResponse> userTasks;

    public User(){}

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setUserTasks(List<TaskResponse> userTasks) {
        this.userTasks = userTasks;
    }
    public List<TaskResponse> getUserTasks() {
        return userTasks;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setRole(Roles role) {
        this.role = role;
    }
    public Roles getRole() {
        return role;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
}

package com.jorchdev.todo_api.repositories;

import com.jorchdev.todo_api.entities.Task;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.entities.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByIdAndUser(Long taskId, User ownerShip);
    Page<Task> findByUser(User ownerShip, Pageable pageable);
    Page<Task> findByUserAndStatus(User ownerShip, Status status, Pageable pageable);
    Page<Task> findByUserAndCreatedAtBetween(User ownerShip, LocalDateTime start, LocalDateTime end, Pageable pageable);
    //Page<Task> findByUserIdAndNameContainingIgnoreCase(Long userId, String nameKeyword, Pageable pageable);
}

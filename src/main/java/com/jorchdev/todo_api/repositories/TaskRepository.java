package com.jorchdev.todo_api.repositories;

import com.jorchdev.todo_api.entities.Task;
import com.jorchdev.todo_api.entities.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByIdAndOwnerShipId(Long userId, Long taskId);
    Page<Task> findByUserId(Long userId, Pageable pageable);
    Page<Task> findByUserIdAndStatus(Long userId, Status status, Pageable pageable);
    Page<Task> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start,LocalDateTime end, Pageable pageable);
    //Page<Task> findByUserIdAndNameContainingIgnoreCase(Long userId, String nameKeyword, Pageable pageable);
}

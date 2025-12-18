package com.jorchdev.todo_api.repositories;

import com.jorchdev.todo_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById (Long userId, Long principalId);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}

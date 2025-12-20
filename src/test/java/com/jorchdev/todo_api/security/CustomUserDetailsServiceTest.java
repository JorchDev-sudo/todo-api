package com.jorchdev.todo_api.security;

import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.entities.enums.Roles;
import com.jorchdev.todo_api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void shouldLoadUserByEmail() {
        // Arrange
        String email = "john@example.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword("$2a$10$hashedPassword...");
        user.setName("John Doe");
        user.setRole(Roles.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo("$2a$10$hashedPassword...");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");

        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Not found");

        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldLoadAdminUser() {
        // Arrange
        String email = "admin@example.com";

        User admin = new User();
        admin.setId(2L);
        admin.setEmail(email);
        admin.setPassword("$2a$10$adminPassword...");
        admin.setName("Admin User");
        admin.setRole(Roles.ADMIN);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(admin));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }
}
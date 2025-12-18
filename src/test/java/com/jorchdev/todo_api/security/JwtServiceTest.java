package com.jorchdev.todo_api.security;

import com.jorchdev.todo_api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
    }

    @Test
    void shouldGenerateToken() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setName("John Doe");

        // Act
        String token = jwtService.generateToken(user);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void shouldExtractEmailFromToken() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setName("John Doe");

        String token = jwtService.generateToken(user);

        // Act
        String email = jwtService.extractEmail(token);

        // Assert
        assertThat(email).isEqualTo("john@example.com");
    }

    @Test
    void shouldExtractUserIdFromToken() {
        // Arrange
        User user = new User();
        user.setId(42L);
        user.setEmail("john@example.com");
        user.setName("John Doe");

        String token = jwtService.generateToken(user);

        // Act
        Long userId = jwtService.extractUserId(token);

        // Assert
        assertThat(userId).isEqualTo(42L);
    }

    @Test
    void shouldValidateToken() {
        // Arrange
        User user = new User();
        user.setEmail("john@example.com");
        user.setName("John");

        String token = jwtService.generateToken(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("john@example.com")
                .password("password")
                .roles("USER")
                .build();

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertThat(isValid).isTrue();
    }
/*
    @Test
    void shouldReturnFalseForExpiredToken() {
        // Arrange
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1L);

        User user = new User();
        user.setEmail("john@example.com");
        user.setName("John");

        String token = jwtService.generateToken(user);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Act
        boolean isExpired = jwtService.isTokenExpired(token);

        // Assert
        assertThat(isExpired).isTrue();
    }
    */
}
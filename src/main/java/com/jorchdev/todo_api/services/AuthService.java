package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.LoginRequest;
import com.jorchdev.todo_api.dto.request.RegisterRequest;
import com.jorchdev.todo_api.dto.response.LoginResponse;
import com.jorchdev.todo_api.dto.response.RegisterResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.repositories.UserRepository;
import com.jorchdev.todo_api.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtservice
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtservice;
    }

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);


        userRepository.save(user);
        return new RegisterResponse("User created successfully", user.getEmail(), user.getName());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(token, user.getEmail(), user.getName());
    }
}
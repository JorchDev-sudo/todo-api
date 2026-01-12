package com.jorchdev.todo_api.services;

import com.jorchdev.todo_api.dto.request.CreateUserRequest;
import com.jorchdev.todo_api.dto.request.DeleteAccountRequest;
import com.jorchdev.todo_api.dto.request.UpdateUserRequest;
import com.jorchdev.todo_api.dto.response.UserResponse;
import com.jorchdev.todo_api.entities.User;
import com.jorchdev.todo_api.exceptions.ForbiddenException;
import com.jorchdev.todo_api.mappers.UserMapper;
import com.jorchdev.todo_api.repositories.UserRepository;
import com.jorchdev.todo_api.utils.DeleteAccountConstants;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final Set<String> VALID_SORT_FIELDS = Set.of("id", "name");

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse findUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userMapper.toResponse(user);
    }

    public Page<UserResponse> findPagedUsers(int page, int size, String sortBy, String direction) {
        if (!VALID_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> usersPage = userRepository.findAll(pageable);

        return usersPage.map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse updateUser(User currentUser,
                                   UpdateUserRequest request) {

        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String hashedPassword = passwordEncoder.encode(request.password);

        userMapper.updateEntity(user, request, hashedPassword);

        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteCurrentUser(DeleteAccountRequest request, User user) {
        if (!DeleteAccountConstants.CONFIRMATION_PHRASE
                .equals(request.getConfirmation())) {

            throw new ForbiddenException(
                    "Confirmation phrase does not match"
            );
        }

        try {
            userRepository.delete(user);
        } catch (EntityNotFoundException e){
            e.getMessage();

        } catch (Exception e){
            e.getMessage();

        }
    }

}

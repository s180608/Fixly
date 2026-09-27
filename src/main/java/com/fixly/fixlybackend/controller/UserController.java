package com.fixly.fixlybackend.controller;

import com.fixly.fixlybackend.model.User;
import com.fixly.fixlybackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.fixly.fixlybackend.dto.UserResponse;
import com.fixly.fixlybackend.dto.UserCreateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers()
                .stream()
                .map(userService::toUserResponse)
                .toList();
    }

    @PostMapping
    public UserResponse createUser(
            @RequestBody UserCreateRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());

        User createdUser = userService.createUser(user);

        return userService.toUserResponse(createdUser);
    }
    @GetMapping("/{id}")
    public UserResponse getUserById(
            @PathVariable Long id,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin &&
                !userService.isUserAccountOwner(
                        id,
                        authentication.getName())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only view your own profile"
            );
        }

        User user = userService.getUserById(id);

        return userService.toUserResponse(user);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @RequestBody User user,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin &&
                !userService.isUserAccountOwner(
                        id,
                        authentication.getName())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only update your own profile"
            );
        }

        User updatedUser = userService.updateUser(id, user);

        return userService.toUserResponse(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable Long id,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin &&
                !userService.isUserAccountOwner(
                        id,
                        authentication.getName())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only delete your own account"
            );
        }

        userService.deleteUser(id);
    }


}
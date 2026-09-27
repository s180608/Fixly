package com.fixly.fixlybackend.controller;

import com.fixly.fixlybackend.dto.LoginRequest;
import com.fixly.fixlybackend.model.User;
import com.fixly.fixlybackend.service.UserService;
import org.springframework.web.bind.annotation.*;
import com.fixly.fixlybackend.dto.LoginResponse;
import com.fixly.fixlybackend.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            JwtService jwtService) {

        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {

        User user = userService.login(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );


        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
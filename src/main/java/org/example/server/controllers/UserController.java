package org.example.server.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.dtos.UserDto;
import org.example.server.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
public class UserController {

    private final UserService userService;

    @PostMapping
    private ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto newUser, Authentication auth) {
        String userAuth = auth.getName();

        userService.createUser(userAuth, newUser);
        return ResponseEntity.status(201).build();
    }
}

package org.example.server.controllers;

import jakarta.validation.Valid;
import org.example.server.dtos.UserDto;
import org.example.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    private ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto newUser, Authentication auth) {
        String userAuth = auth.getName();

        // Create user service and perform error handling
        UserDto createdUser = userService.createUser(userAuth, newUser);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping
    private String sayHi(){
        return "Hello World";
    }

}

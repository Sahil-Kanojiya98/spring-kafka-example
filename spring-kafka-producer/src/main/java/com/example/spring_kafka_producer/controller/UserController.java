package com.example.spring_kafka_producer.controller;

import com.example.spring_kafka_producer.dto.request.CreateUserRequest;
import com.example.spring_kafka_producer.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void run() {
        userService.createUser(new CreateUserRequest("sahilkanojiya@gmail.com", "SahilKanojiya", "S@hil"));
        for (int i = 1; i <= 30; i++) {
            String email = "user" + i + "@example.com";
            String username = "User" + i;
            String password = "Pass@123" + i;
            userService.createUser(new CreateUserRequest(email, username, password));
        }
    }

    @PostMapping("/")
    public ResponseEntity<Void> createUser(@RequestBody CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @PostMapping("/duplicate")
    public ResponseEntity<Void> createDuplicateUser(@RequestBody CreateUserRequest createUserRequest) {
        userService.createDuplicateUser(createUserRequest);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }
}

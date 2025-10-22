package com.example.userservice.controllers;

import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.services.KeycloakUserService;
import com.example.userservice.services.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KeycloakUserService keycloakUserService;


    public AuthController(KeycloakUserService keycloakUserService) {

        this.keycloakUserService = keycloakUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        keycloakUserService.createUser(request);
        return ResponseEntity.ok("User created successfully!");
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(keycloakUserService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        return ResponseEntity.ok(keycloakUserService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        keycloakUserService.updateUser(id, updates);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        keycloakUserService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }



}
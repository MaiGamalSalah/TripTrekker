package com.example.userservice.controllers;

import com.example.userservice.services.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Map<String, Object> tokens = loginService.login(username, password);

        return ResponseEntity.ok(tokens);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        loginService.logout(body.get("refresh_token"));
        return ResponseEntity.ok("Logged out successfully");
    }

}
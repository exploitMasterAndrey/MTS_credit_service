package com.example.creditservice.controller.impl;

import com.example.creditservice.controller.AuthController;
import com.example.creditservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-service")
public class AuthControllerImpl implements AuthController {
    private final AuthService authService;
    @Override
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        String jwt = authService.login(authRequest);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}

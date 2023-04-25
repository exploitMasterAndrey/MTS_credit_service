package com.example.creditservice.service;

import com.example.creditservice.controller.AuthController;

public interface AuthService {
    String login(AuthController.AuthRequest authRequest);
}

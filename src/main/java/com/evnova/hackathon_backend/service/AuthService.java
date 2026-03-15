package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.AuthResponse;
import com.evnova.hackathon_backend.dto.LoginRequest;
import com.evnova.hackathon_backend.dto.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse.UserDTO getCurrentUser();
}

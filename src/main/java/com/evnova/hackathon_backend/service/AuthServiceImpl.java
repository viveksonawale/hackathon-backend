package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.AuthResponse;
import com.evnova.hackathon_backend.dto.LoginRequest;
import com.evnova.hackathon_backend.dto.SignupRequest;
import com.evnova.hackathon_backend.enums.Role;
import com.evnova.hackathon_backend.exception.UnauthorizedException;
import com.evnova.hackathon_backend.exception.ValidationException;
import com.evnova.hackathon_backend.model.User;
import com.evnova.hackathon_backend.repository.UserRepository;
import com.evnova.hackathon_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email is already in use.");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().name());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ValidationException("Invalid role specified.");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);
        
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, mapToUserDTO(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials."));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, mapToUserDTO(user));
    }

    @Override
    public AuthResponse.UserDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("Not authenticated.");
        }

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found."));
                
        return mapToUserDTO(user);
    }
    
    private AuthResponse.UserDTO mapToUserDTO(User user) {
        return new AuthResponse.UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }
}

package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.UserProfileDTO;
import com.evnova.hackathon_backend.exception.UnauthorizedException;
import com.evnova.hackathon_backend.model.User;
import com.evnova.hackathon_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @Override
    public UserProfileDTO.Response getMe() {
        return map(getAuthenticatedUser());
    }

    @Override
    public UserProfileDTO.Response updateMe(UserProfileDTO.UpdateRequest request) {
        User user = getAuthenticatedUser();
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }
        user = userRepository.save(user);
        return map(user);
    }

    private UserProfileDTO.Response map(User user) {
        return new UserProfileDTO.Response(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}

package com.evnova.hackathon_backend.controller;

import com.evnova.hackathon_backend.dto.UserProfileDTO;
import com.evnova.hackathon_backend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserProfileService userProfileService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('ROLE_PARTICIPANT','ROLE_ORGANIZER')")
    public ResponseEntity<UserProfileDTO.Response> getMe() {
        return ResponseEntity.ok(userProfileService.getMe());
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAnyAuthority('ROLE_PARTICIPANT','ROLE_ORGANIZER')")
    public ResponseEntity<UserProfileDTO.Response> updateMe(@RequestBody UserProfileDTO.UpdateRequest request) {
        return ResponseEntity.ok(userProfileService.updateMe(request));
    }
}

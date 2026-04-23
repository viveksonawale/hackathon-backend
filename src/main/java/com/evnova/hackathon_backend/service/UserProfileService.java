package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.UserProfileDTO;

public interface UserProfileService {
    UserProfileDTO.Response getMe();
    UserProfileDTO.Response updateMe(UserProfileDTO.UpdateRequest request);
}

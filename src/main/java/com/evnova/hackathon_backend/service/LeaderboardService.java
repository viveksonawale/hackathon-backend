package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.SubmissionDTO;
import java.util.List;

public interface LeaderboardService {
    List<SubmissionDTO.Response> getLeaderboard(Long hackathonId);
}
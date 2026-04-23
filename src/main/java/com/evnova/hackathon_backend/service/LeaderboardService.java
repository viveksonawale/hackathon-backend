package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.LeaderboardDTO;
import java.util.List;

public interface LeaderboardService {
    List<LeaderboardDTO.Entry> getLeaderboard(Long hackathonId);
}
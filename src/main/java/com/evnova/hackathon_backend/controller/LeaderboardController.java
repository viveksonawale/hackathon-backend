package com.evnova.hackathon_backend.controller;

import com.evnova.hackathon_backend.dto.LeaderboardDTO;
import com.evnova.hackathon_backend.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hackathons")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/{hackathonId}/leaderboard")
    public ResponseEntity<List<LeaderboardDTO.Entry>> getLeaderboard(@PathVariable Long hackathonId) {
        return ResponseEntity.ok(leaderboardService.getLeaderboard(hackathonId));
    }
}

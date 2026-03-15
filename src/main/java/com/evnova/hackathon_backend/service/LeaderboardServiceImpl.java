package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.SubmissionDTO;
import com.evnova.hackathon_backend.exception.ResourceNotFoundException;
import com.evnova.hackathon_backend.model.Hackathon;
import com.evnova.hackathon_backend.model.Submission;
import com.evnova.hackathon_backend.repository.HackathonRepository;
import com.evnova.hackathon_backend.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final SubmissionRepository submissionRepository;
    private final HackathonRepository hackathonRepository;

    @Override
    public List<SubmissionDTO.Response> getLeaderboard(Long hackathonId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));
        
        List<Submission> submissions = submissionRepository.findByHackathonOrderByScoreDesc(hackathon);
        
        return submissions.stream()
                .map(s -> new SubmissionDTO.Response(
                        s.getId(),
                        s.getTeam().getId(),
                        s.getTeam().getName(),
                        s.getHackathon().getId(),
                        s.getProjectName(),
                        s.getDescription(),
                        s.getGithubUrl(),
                        s.getDemoUrl(),
                        s.getPresentationUrl(),
                        s.getSubmittedAt(),
                        s.getScore()
                ))
                .collect(Collectors.toList());
    }
}

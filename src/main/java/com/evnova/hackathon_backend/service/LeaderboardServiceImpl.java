package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.LeaderboardDTO;
import com.evnova.hackathon_backend.dto.TeamDTO;
import com.evnova.hackathon_backend.exception.ResourceNotFoundException;
import com.evnova.hackathon_backend.model.Hackathon;
import com.evnova.hackathon_backend.model.Submission;
import com.evnova.hackathon_backend.repository.HackathonRepository;
import com.evnova.hackathon_backend.repository.SubmissionRepository;
import com.evnova.hackathon_backend.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final SubmissionRepository submissionRepository;
    private final HackathonRepository hackathonRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public List<LeaderboardDTO.Entry> getLeaderboard(Long hackathonId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));
        
        List<Submission> submissions = submissionRepository.findByHackathonOrderByScoreDesc(hackathon).stream()
                .sorted((a, b) -> {
                    int scoreCmp = Double.compare(b.getScore() == null ? 0.0 : b.getScore(), a.getScore() == null ? 0.0 : a.getScore());
                    if (scoreCmp != 0) return scoreCmp;
                    return Long.compare(a.getTeam().getId(), b.getTeam().getId());
                })
                .collect(Collectors.toList());

        int currentRank = 0;
        Double lastScore = null;
        List<LeaderboardDTO.Entry> entries = new java.util.ArrayList<>();
        for (int i = 0; i < submissions.size(); i++) {
            Submission s = submissions.get(i);
            s.setScore(s.getScore() == null ? 0.0 : s.getScore());
            if (lastScore == null || Double.compare(lastScore, s.getScore()) != 0) {
                currentRank = i + 1;
                lastScore = s.getScore();
            }
            entries.add(new LeaderboardDTO.Entry(
                    currentRank,
                    s.getTeam().getId(),
                    s.getTeam().getName(),
                    s.getScore(),
                    teamMemberRepository.findByTeam(s.getTeam()).stream()
                            .map(m -> new TeamDTO.Response.MemberInfo(
                                    m.getUser().getId(),
                                    m.getUser().getName(),
                                    m.getUser().getEmail(),
                                    m.getRole()
                            ))
                            .collect(Collectors.toList())
            ));
        }
        return entries;
    }
}

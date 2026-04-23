package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.SubmissionDTO;
import com.evnova.hackathon_backend.dto.TeamDTO;
import com.evnova.hackathon_backend.exception.ResourceNotFoundException;
import com.evnova.hackathon_backend.exception.UnauthorizedException;
import com.evnova.hackathon_backend.exception.ValidationException;
import com.evnova.hackathon_backend.model.Hackathon;
import com.evnova.hackathon_backend.model.Submission;
import com.evnova.hackathon_backend.model.Team;
import com.evnova.hackathon_backend.model.TeamMember;
import com.evnova.hackathon_backend.model.User;
import com.evnova.hackathon_backend.repository.HackathonRepository;
import com.evnova.hackathon_backend.repository.SubmissionRepository;
import com.evnova.hackathon_backend.repository.TeamMemberRepository;
import com.evnova.hackathon_backend.repository.TeamRepository;
import com.evnova.hackathon_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @Override
    public SubmissionDTO.Response submitProject(Long hackathonId, SubmissionDTO.Request request) {
        User user = getAuthenticatedUser();
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));
        if (hackathon.getEndDate() != null && LocalDate.now().isAfter(hackathon.getEndDate())) {
            throw new ValidationException("Submission deadline has passed");
        }

        // Find the team the user leads for this hackathon
        Team team = teamRepository.findByLeader(user).stream()
                .filter(t -> t.getHackathon().getId().equals(hackathonId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Only team leaders can submit projects"));

        if (submissionRepository.findByTeam(team).isPresent()) {
            throw new ValidationException("Project already submitted for this team");
        }

        Submission submission = Submission.builder()
                .projectName(request.getProjectName())
                .description(request.getDescription())
                .githubUrl(request.getGithubUrl())
                .demoUrl(request.getDemoUrl())
                .presentationUrl(request.getPresentationUrl())
                .team(team)
                .hackathon(hackathon)
                .submittedAt(LocalDateTime.now())
                .score(0.0)
                .build();

        submission = submissionRepository.save(submission);
        return mapToDTO(submission);
    }

    @Override
    public SubmissionDTO.Response updateSubmission(Long submissionId, SubmissionDTO.Request request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        User user = getAuthenticatedUser();
        if (!submission.getTeam().getLeader().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the team leader can update the submission");
        }
        if (submission.getHackathon().getEndDate() != null && LocalDate.now().isAfter(submission.getHackathon().getEndDate())) {
            throw new ValidationException("Submission updates are closed after deadline");
        }

        submission.setProjectName(request.getProjectName());
        submission.setDescription(request.getDescription());
        submission.setGithubUrl(request.getGithubUrl());
        submission.setDemoUrl(request.getDemoUrl());
        submission.setPresentationUrl(request.getPresentationUrl());

        submission = submissionRepository.save(submission);
        return mapToDTO(submission);
    }

    @Override
    public SubmissionDTO.Response getSubmissionById(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        User user = getAuthenticatedUser();
        
        boolean isOrganizer = submission.getHackathon().getOrganizer().getId().equals(user.getId());
        boolean isMember = teamMemberRepository.existsByTeamAndUser(submission.getTeam(), user);
        
        if (!isOrganizer && !isMember) {
            throw new UnauthorizedException("You are not allowed to view this submission");
        }
        return mapToDTO(submission);
    }

    @Override
    public SubmissionDTO.Response getTeamSubmission(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        Submission submission = submissionRepository.findByTeam(team)
                .orElseThrow(() -> new ResourceNotFoundException("No submission found for this team"));
        return mapToDTO(submission);
    }

    @Override
    public SubmissionDTO.Response getMySubmission(Long hackathonId) {
        User user = getAuthenticatedUser();
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));
        
        // Find team
        Team team = teamMemberRepository.findFirstByUserAndTeam_Hackathon(user, hackathon)
                .map(TeamMember::getTeam)
                .orElseThrow(() -> new ResourceNotFoundException("You are not in a team for this hackathon"));
        
        // Find submission
        return submissionRepository.findByTeamAndHackathon(team, hackathon)
                .map(this::mapToDTO)
                .orElse(null); // Return null if not yet submitted instead of throwing 404
    }

    @Override
    public List<SubmissionDTO.Response> getHackathonSubmissions(Long hackathonId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));
        return submissionRepository.findByHackathon(hackathon).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void scoreSubmission(Long submissionId, Double score) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        User user = getAuthenticatedUser();
        if (!submission.getHackathon().getOrganizer().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the hackathon organizer can score submissions");
        }

        submission.setScore(score);
        submissionRepository.save(submission);
    }

    private SubmissionDTO.Response mapToDTO(Submission s) {
        return new SubmissionDTO.Response(
                s.getId(),
                s.getTeam().getId(),
                s.getTeam().getName(),
                s.getHackathon().getId(),
                new TeamDTO.Response.LeaderInfo(
                        s.getTeam().getLeader().getId(),
                        s.getTeam().getLeader().getName(),
                        s.getTeam().getLeader().getEmail()
                ),
                teamMemberRepository.findByTeam(s.getTeam()).stream()
                        .map(m -> new TeamDTO.Response.MemberInfo(
                                m.getUser().getId(),
                                m.getUser().getName(),
                                m.getUser().getEmail(),
                                m.getRole()
                        ))
                        .collect(Collectors.toList()),
                s.getProjectName(),
                s.getDescription(),
                s.getGithubUrl(),
                s.getDemoUrl(),
                s.getPresentationUrl(),
                s.getSubmittedAt(),
                s.getScore()
        );
    }
}

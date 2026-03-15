package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.TeamDTO;
import com.evnova.hackathon_backend.exception.ResourceNotFoundException;
import com.evnova.hackathon_backend.exception.UnauthorizedException;
import com.evnova.hackathon_backend.exception.ValidationException;
import com.evnova.hackathon_backend.model.*;
import com.evnova.hackathon_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @Override
    @Transactional
    public TeamDTO.Response createTeam(Long hackathonId, TeamDTO.Request request) {
        User leader = getAuthenticatedUser();
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));

        if (!participantRepository.existsByUserAndHackathon(leader, hackathon)) {
            throw new ValidationException("You must join the hackathon before creating a team");
        }

        if (teamRepository.existsByHackathonAndLeader(hackathon, leader)) {
            throw new ValidationException("You are already leading a team in this hackathon");
        }

        Team team = Team.builder()
                .name(request.getName())
                .hackathon(hackathon)
                .leader(leader)
                .createdAt(LocalDateTime.now())
                .build();

        team = teamRepository.save(team);

        TeamMember member = TeamMember.builder()
                .user(leader)
                .team(team)
                .role("LEADER")
                .build();

        teamMemberRepository.save(member);

        return mapToDTO(team);
    }

    @Override
    public TeamDTO.Response updateTeam(Long teamId, TeamDTO.Request request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        
        User leader = getAuthenticatedUser();
        if (!team.getLeader().getId().equals(leader.getId())) {
            throw new UnauthorizedException("Only the team leader can update team details");
        }

        team.setName(request.getName());
        team = teamRepository.save(team);
        return mapToDTO(team);
    }

    @Override
    public TeamDTO.Response getTeamById(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        return mapToDTO(team);
    }

    @Override
    public List<TeamDTO.Response> getTeamsByHackathon(Long hackathonId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));
        return teamRepository.findByHackathon(hackathon).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TeamDTO.Response getMyTeam(Long hackathonId) {
        User user = getAuthenticatedUser();
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));
        
        // Find if user is a member of any team in this hackathon
        return teamMemberRepository.findAll().stream()
                .filter(tm -> tm.getUser().getId().equals(user.getId()) && tm.getTeam().getHackathon().getId().equals(hackathonId))
                .findFirst()
                .map(tm -> mapToDTO(tm.getTeam()))
                .orElseThrow(() -> new ResourceNotFoundException("You are not in a team for this hackathon"));
    }

    @Override
    public void inviteMember(Long teamId, TeamDTO.InviteRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        
        User leader = getAuthenticatedUser();
        if (!team.getLeader().getId().equals(leader.getId())) {
            throw new UnauthorizedException("Only the team leader can invite members");
        }

        User invitee = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invited user not found"));

        if (!participantRepository.existsByUserAndHackathon(invitee, team.getHackathon())) {
            throw new ValidationException("The user must join the hackathon first");
        }

        if (teamMemberRepository.existsByTeamAndUser(team, invitee)) {
            throw new ValidationException("User is already a member of this team");
        }

        // Check max team size
        long currentMembers = teamMemberRepository.findByTeam(team).size();
        if (currentMembers >= team.getHackathon().getMaxTeamSize()) {
            throw new ValidationException("Team size limit reached");
        }

        TeamMember member = TeamMember.builder()
                .user(invitee)
                .team(team)
                .role("MEMBER")
                .build();

        teamMemberRepository.save(member);
    }

    @Override
    @Transactional
    public void removeMember(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        
        User leader = getAuthenticatedUser();
        if (!team.getLeader().getId().equals(leader.getId())) {
            throw new UnauthorizedException("Only the team leader can remove members");
        }

        if (team.getLeader().getId().equals(userId)) {
            throw new ValidationException("Leader cannot be removed. Delete the team instead.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        teamMemberRepository.deleteByTeamAndUser(team, user);
    }

    @Override
    @Transactional
    public void leaveTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        
        User user = getAuthenticatedUser();
        if (team.getLeader().getId().equals(user.getId())) {
            throw new ValidationException("Leader cannot leave the team. Transfer leadership or delete the team.");
        }

        teamMemberRepository.deleteByTeamAndUser(team, user);
    }

    @Override
    @Transactional
    public void deleteTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        
        User leader = getAuthenticatedUser();
        if (!team.getLeader().getId().equals(leader.getId())) {
            throw new UnauthorizedException("Only the team leader can delete the team");
        }

        teamRepository.delete(team);
    }

    private TeamDTO.Response mapToDTO(Team team) {
        List<TeamMember> members = teamMemberRepository.findByTeam(team);
        
        List<TeamDTO.Response.MemberInfo> memberInfos = members.stream()
                .map(m -> new TeamDTO.Response.MemberInfo(
                        m.getUser().getId(),
                        m.getUser().getName(),
                        m.getUser().getEmail(),
                        m.getRole()
                ))
                .collect(Collectors.toList());

        return new TeamDTO.Response(
                team.getId(),
                team.getName(),
                team.getHackathon().getId(),
                team.getCreatedAt(),
                new TeamDTO.Response.LeaderInfo(
                        team.getLeader().getId(),
                        team.getLeader().getName(),
                        team.getLeader().getEmail()
                ),
                memberInfos
        );
    }
}

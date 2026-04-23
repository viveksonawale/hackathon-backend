package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.InvitationDTO;
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
    private final InvitationRepository invitationRepository;
    private final NotificationService notificationService;

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
        if (teamMemberRepository.existsByUserAndTeam_Hackathon(leader, hackathon)) {
            throw new ValidationException("You are already in a team in this hackathon");
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
        User user = getAuthenticatedUser();
        
        boolean isOrganizer = team.getHackathon().getOrganizer().getId().equals(user.getId()) 
                || user.getRole().name().equals("ORGANIZER"); // Allow any organizer for now
        boolean isMember = teamMemberRepository.existsByTeamAndUser(team, user);
        
        if (!isOrganizer && !isMember) {
            throw new UnauthorizedException("You are not allowed to view this team");
        }
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
        
        return teamMemberRepository.findFirstByUserAndTeam_Hackathon(user, hackathon)
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
        if (teamMemberRepository.existsByUserAndTeam_Hackathon(invitee, team.getHackathon())) {
            throw new ValidationException("User is already in another team in this hackathon");
        }
        if (invitationRepository.existsByTeamAndInvitedUserAndStatus(team, invitee, "PENDING")) {
            throw new ValidationException("A pending invitation already exists for this user");
        }

        // Check max team size
        long currentMembers = teamMemberRepository.findByTeam(team).size();
        if (currentMembers >= team.getHackathon().getMaxTeamSize()) {
            throw new ValidationException("Team size limit reached");
        }

        Invitation invitation = Invitation.builder()
                .team(team)
                .invitedUser(invitee)
                .invitedBy(leader)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        invitationRepository.save(invitation);
        notificationService.createForUser(
                invitee.getId(),
                "INVITATION",
                "You have been invited to join team " + team.getName()
        );
    }

    @Override
    public List<InvitationDTO.Response> getMyInvitations() {
        User user = getAuthenticatedUser();
        return invitationRepository.findByInvitedUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapInvitationToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamDTO.Response acceptInvitation(Long invitationId) {
        User user = getAuthenticatedUser();
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));
        if (!invitation.getInvitedUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only invited user can accept this invitation");
        }
        if (!"PENDING".equals(invitation.getStatus())) {
            throw new ValidationException("Invitation is already processed");
        }
        Team team = invitation.getTeam();
        if (teamMemberRepository.existsByUserAndTeam_Hackathon(user, team.getHackathon())) {
            throw new ValidationException("You are already in a team in this hackathon");
        }
        long currentMembers = teamMemberRepository.findByTeam(team).size();
        if (currentMembers >= team.getHackathon().getMaxTeamSize()) {
            throw new ValidationException("Team size limit reached");
        }
        teamMemberRepository.save(TeamMember.builder().team(team).user(user).role("MEMBER").build());
        invitation.setStatus("ACCEPTED");
        invitation.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(invitation);
        notificationService.createForUser(
                invitation.getInvitedBy().getId(),
                "INVITATION_ACCEPTED",
                user.getName() + " accepted your invitation to " + team.getName()
        );
        return mapToDTO(team);
    }

    @Override
    public void rejectInvitation(Long invitationId) {
        User user = getAuthenticatedUser();
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));
        if (!invitation.getInvitedUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only invited user can reject this invitation");
        }
        if (!"PENDING".equals(invitation.getStatus())) {
            throw new ValidationException("Invitation is already processed");
        }
        invitation.setStatus("REJECTED");
        invitation.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(invitation);
        notificationService.createForUser(
                invitation.getInvitedBy().getId(),
                "INVITATION_REJECTED",
                user.getName() + " rejected your invitation to " + invitation.getTeam().getName()
        );
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
                new TeamDTO.Response.HackathonInfo(
                        team.getHackathon().getId(),
                        team.getHackathon().getTitle(),
                        team.getHackathon().getStartDate(),
                        team.getHackathon().getEndDate(),
                        team.getHackathon().getStatus()
                ),
                team.getCreatedAt(),
                new TeamDTO.Response.LeaderInfo(
                        team.getLeader().getId(),
                        team.getLeader().getName(),
                        team.getLeader().getEmail()
                ),
                memberInfos
        );
    }

    private InvitationDTO.Response mapInvitationToDTO(Invitation invitation) {
        return new InvitationDTO.Response(
                invitation.getId(),
                invitation.getStatus(),
                invitation.getCreatedAt(),
                invitation.getTeam().getId(),
                invitation.getTeam().getName(),
                invitation.getTeam().getHackathon().getId(),
                invitation.getTeam().getHackathon().getTitle(),
                new InvitationDTO.UserBasic(
                        invitation.getInvitedBy().getId(),
                        invitation.getInvitedBy().getName(),
                        invitation.getInvitedBy().getEmail()
                )
        );
    }
}

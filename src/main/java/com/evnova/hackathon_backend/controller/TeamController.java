package com.evnova.hackathon_backend.controller;

import com.evnova.hackathon_backend.dto.TeamDTO;
import com.evnova.hackathon_backend.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/participant/hackathons/{hackathonId}/teams")
    @PreAuthorize("hasAuthority('ROLE_PARTICIPANT')")
    public ResponseEntity<TeamDTO.Response> createTeam(
            @PathVariable Long hackathonId,
            @Valid @RequestBody TeamDTO.Request request) {
        return ResponseEntity.ok(teamService.createTeam(hackathonId, request));
    }

    @GetMapping("/hackathons/{hackathonId}/teams")
    public ResponseEntity<List<TeamDTO.Response>> getTeamsByHackathon(@PathVariable Long hackathonId) {
        return ResponseEntity.ok(teamService.getTeamsByHackathon(hackathonId));
    }

    @GetMapping("/participant/hackathons/{hackathonId}/my-team")
    @PreAuthorize("hasAuthority('ROLE_PARTICIPANT')")
    public ResponseEntity<TeamDTO.Response> getMyTeam(@PathVariable Long hackathonId) {
        return ResponseEntity.ok(teamService.getMyTeam(hackathonId));
    }

    @PutMapping("/participant/teams/{teamId}")
    @PreAuthorize("hasAuthority('ROLE_PARTICIPANT')")
    public ResponseEntity<TeamDTO.Response> updateTeam(
            @PathVariable Long teamId,
            @Valid @RequestBody TeamDTO.Request request) {
        return ResponseEntity.ok(teamService.updateTeam(teamId, request));
    }

    @PostMapping("/participant/teams/{teamId}/invite")
    @PreAuthorize("hasAuthority('ROLE_PARTICIPANT')")
    public ResponseEntity<Void> inviteMember(
            @PathVariable Long teamId,
            @Valid @RequestBody TeamDTO.InviteRequest request) {
        teamService.inviteMember(teamId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/participant/teams/{teamId}/members/{userId}")
    @PreAuthorize("hasAuthority('ROLE_PARTICIPANT')")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long teamId,
            @PathVariable Long userId) {
        teamService.removeMember(teamId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/participant/teams/{teamId}/leave")
    @PreAuthorize("hasAuthority('ROLE_PARTICIPANT')")
    public ResponseEntity<Void> leaveTeam(@PathVariable Long teamId) {
        teamService.leaveTeam(teamId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/participant/teams/{teamId}")
    @PreAuthorize("hasAuthority('ROLE_PARTICIPANT')")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.noContent().build();
    }
}

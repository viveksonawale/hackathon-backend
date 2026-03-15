package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.TeamDTO;

import java.util.List;

public interface TeamService {
    TeamDTO.Response createTeam(Long hackathonId, TeamDTO.Request request);
    TeamDTO.Response updateTeam(Long teamId, TeamDTO.Request request);
    TeamDTO.Response getTeamById(Long teamId);
    List<TeamDTO.Response> getTeamsByHackathon(Long hackathonId);
    TeamDTO.Response getMyTeam(Long hackathonId);
    void inviteMember(Long teamId, TeamDTO.InviteRequest request);
    void removeMember(Long teamId, Long userId);
    void leaveTeam(Long teamId);
    void deleteTeam(Long teamId);
}

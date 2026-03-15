package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.HackathonDTO;

import java.util.List;

public interface HackathonService {
    HackathonDTO.Response createHackathon(HackathonDTO.Request request);
    HackathonDTO.Response updateHackathon(Long id, HackathonDTO.Request request);
    void deleteHackathon(Long id);
    HackathonDTO.Response getHackathonById(Long id);
    List<HackathonDTO.Response> getAllHackathons(String status);
    List<HackathonDTO.Response> getMyGeneratedHackathons();
    List<HackathonDTO.Response> getMyJoinedHackathons();
    void joinHackathon(Long id);
}

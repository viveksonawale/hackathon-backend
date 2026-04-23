package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.HackathonDTO;
import com.evnova.hackathon_backend.dto.PagedResponse;

import java.util.List;

public interface HackathonService {
    HackathonDTO.Response createHackathon(HackathonDTO.Request request);
    HackathonDTO.Response updateHackathon(Long id, HackathonDTO.Request request);
    void deleteHackathon(Long id);
    HackathonDTO.Response getHackathonById(Long id);
    PagedResponse<HackathonDTO.Response> getAllHackathons(String status, String search, int page, int size);
    List<HackathonDTO.Response> getMyGeneratedHackathons();
    List<HackathonDTO.Response> getMyJoinedHackathons();
    void joinHackathon(Long id);
}

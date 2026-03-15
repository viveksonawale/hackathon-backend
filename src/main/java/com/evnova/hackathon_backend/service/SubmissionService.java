package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.SubmissionDTO;

import java.util.List;

public interface SubmissionService {
    SubmissionDTO.Response submitProject(Long hackathonId, SubmissionDTO.Request request);
    SubmissionDTO.Response updateSubmission(Long submissionId, SubmissionDTO.Request request);
    SubmissionDTO.Response getSubmissionById(Long submissionId);
    SubmissionDTO.Response getTeamSubmission(Long teamId);
    List<SubmissionDTO.Response> getHackathonSubmissions(Long hackathonId);
    void scoreSubmission(Long submissionId, Double score);
}

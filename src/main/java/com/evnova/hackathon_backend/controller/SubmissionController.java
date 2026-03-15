package com.evnova.hackathon_backend.controller;

import com.evnova.hackathon_backend.dto.SubmissionDTO;
import com.evnova.hackathon_backend.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/participant/hackathons/{hackathonId}/submissions")
    @PreAuthorize("hasAuthority('ROLE_PARTICIPANT')")
    public ResponseEntity<SubmissionDTO.Response> submitProject(
            @PathVariable Long hackathonId,
            @Valid @RequestBody SubmissionDTO.Request request) {
        return ResponseEntity.ok(submissionService.submitProject(hackathonId, request));
    }

    @PutMapping("/participant/submissions/{submissionId}")
    @PreAuthorize("hasAuthority('ROLE_PARTICIPANT')")
    public ResponseEntity<SubmissionDTO.Response> updateSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody SubmissionDTO.Request request) {
        return ResponseEntity.ok(submissionService.updateSubmission(submissionId, request));
    }

    @GetMapping("/organizer/hackathons/{hackathonId}/submissions")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<List<SubmissionDTO.Response>> getHackathonSubmissions(@PathVariable Long hackathonId) {
        return ResponseEntity.ok(submissionService.getHackathonSubmissions(hackathonId));
    }

    @PostMapping("/organizer/submissions/{submissionId}/score")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<Void> scoreSubmission(
            @PathVariable Long submissionId,
            @RequestParam Double score) {
        submissionService.scoreSubmission(submissionId, score);
        return ResponseEntity.ok().build();
    }
}

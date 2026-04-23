package com.evnova.hackathon_backend.controller;

import com.evnova.hackathon_backend.dto.HackathonDTO;
import com.evnova.hackathon_backend.dto.PagedResponse;
import com.evnova.hackathon_backend.service.HackathonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hackathons")
@RequiredArgsConstructor
public class HackathonController {

    private final HackathonService hackathonService;

    @GetMapping
    public ResponseEntity<PagedResponse<HackathonDTO.Response>> getAllHackathons(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(hackathonService.getAllHackathons(status, search, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HackathonDTO.Response> getHackathonById(@PathVariable Long id) {
        return ResponseEntity.ok(hackathonService.getHackathonById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<HackathonDTO.Response> createHackathon(@Valid @RequestBody HackathonDTO.Request request) {
        return ResponseEntity.ok(hackathonService.createHackathon(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<HackathonDTO.Response> updateHackathon(
            @PathVariable Long id,
            @Valid @RequestBody HackathonDTO.Request request) {
        return ResponseEntity.ok(hackathonService.updateHackathon(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<Void> deleteHackathon(@PathVariable Long id) {
        hackathonService.deleteHackathon(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    @PreAuthorize("hasAuthority('ROLE_PARTICIPANT')")
    public ResponseEntity<Void> joinHackathon(@PathVariable Long id) {
        hackathonService.joinHackathon(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/organized")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<List<HackathonDTO.Response>> getMyOrganizedHackathons() {
        return ResponseEntity.ok(hackathonService.getMyGeneratedHackathons());
    }

    @GetMapping("/me/joined")
    @PreAuthorize("hasAuthority('ROLE_PARTICIPANT')")
    public ResponseEntity<List<HackathonDTO.Response>> getMyJoinedHackathons() {
        return ResponseEntity.ok(hackathonService.getMyJoinedHackathons());
    }
}

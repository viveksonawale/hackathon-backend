package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.HackathonDTO;
import com.evnova.hackathon_backend.dto.PagedResponse;
import com.evnova.hackathon_backend.exception.ResourceNotFoundException;
import com.evnova.hackathon_backend.exception.UnauthorizedException;
import com.evnova.hackathon_backend.exception.ValidationException;
import com.evnova.hackathon_backend.model.Hackathon;
import com.evnova.hackathon_backend.model.Participants;
import com.evnova.hackathon_backend.model.User;
import com.evnova.hackathon_backend.repository.HackathonRepository;
import com.evnova.hackathon_backend.repository.ParticipantRepository;
import com.evnova.hackathon_backend.repository.TeamRepository;
import com.evnova.hackathon_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HackathonServiceImpl implements HackathonService {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final TeamRepository teamRepository;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @Override
    public HackathonDTO.Response createHackathon(HackathonDTO.Request request) {
        User organizer = getAuthenticatedUser();
        
        Hackathon hackathon = Hackathon.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .type(request.getType() != null ? request.getType() : "online")
                .themes(request.getThemes())
                .prizePool(request.getPrizePool())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .registrationDeadline(request.getRegistrationDeadline())
                .maxTeamSize(request.getMaxTeamSize() != null ? request.getMaxTeamSize() : 4)
                .status("UPCOMING")
                .organizer(organizer)
                .problemStatement(request.getProblemStatement())
                .goals(request.getGoals())
                .rules(request.getRules())
                .createdAt(LocalDateTime.now())
                .build();
                
        hackathon = hackathonRepository.save(hackathon);
        return mapToDTO(hackathon);
    }

    @Override
    public HackathonDTO.Response updateHackathon(Long id, HackathonDTO.Request request) {
        Hackathon hackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));
                
        User organizer = getAuthenticatedUser();
        if (!hackathon.getOrganizer().getId().equals(organizer.getId())) {
            throw new UnauthorizedException("You are not the organizer of this hackathon");
        }
        
        hackathon.setTitle(request.getTitle());
        hackathon.setDescription(request.getDescription());
        hackathon.setShortDescription(request.getShortDescription());
        hackathon.setType(request.getType());
        hackathon.setThemes(request.getThemes());
        hackathon.setPrizePool(request.getPrizePool());
        hackathon.setStartDate(request.getStartDate());
        hackathon.setEndDate(request.getEndDate());
        hackathon.setRegistrationDeadline(request.getRegistrationDeadline());
        hackathon.setProblemStatement(request.getProblemStatement());
        hackathon.setGoals(request.getGoals());
        hackathon.setRules(request.getRules());

        if (request.getMaxTeamSize() != null) {
            hackathon.setMaxTeamSize(request.getMaxTeamSize());
        }
        if (request.getStatus() != null) {
            hackathon.setStatus(request.getStatus());
        }
        
        hackathon = hackathonRepository.save(hackathon);
        return mapToDTO(hackathon);
    }

    @Override
    public void deleteHackathon(Long id) {
        Hackathon hackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));
                
        User organizer = getAuthenticatedUser();
        if (!hackathon.getOrganizer().getId().equals(organizer.getId())) {
            throw new UnauthorizedException("You are not the organizer of this hackathon");
        }
        
        hackathonRepository.delete(hackathon);
    }

    @Override
    public HackathonDTO.Response getHackathonById(Long id) {
        Hackathon hackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));
        return mapToDTO(hackathon);
    }

    @Override
    public PagedResponse<HackathonDTO.Response> getAllHackathons(String status, String search, int page, int size) {
        String normalizedStatus = (status == null || status.isBlank()) ? null : status.toUpperCase();
        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();
        List<Hackathon> hackathons = hackathonRepository.findBySearchAndStatus(normalizedSearch, normalizedStatus)
                .stream()
                .sorted(Comparator.comparing(Hackathon::getCreatedAt).reversed())
                .collect(Collectors.toList());
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int start = Math.min(safePage * safeSize, hackathons.size());
        int end = Math.min(start + safeSize, hackathons.size());
        List<HackathonDTO.Response> items = hackathons.subList(start, end).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        int totalPages = (int) Math.ceil((double) hackathons.size() / safeSize);
        return new PagedResponse<>(items, safePage, safeSize, hackathons.size(), totalPages);
    }

    @Override
    public List<HackathonDTO.Response> getMyGeneratedHackathons() {
        User organizer = getAuthenticatedUser();
        List<Hackathon> hackathons = hackathonRepository.findByOrganizer(organizer);
        return hackathons.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<HackathonDTO.Response> getMyJoinedHackathons() {
        User participant = getAuthenticatedUser();
        List<Participants> participations = participantRepository.findByUser(participant);
        return participations.stream()
                .map(p -> mapToDTO(p.getHackathon()))
                .collect(Collectors.toList());
    }

    @Override
    public void joinHackathon(Long id) {
        User participant = getAuthenticatedUser();
        Hackathon hackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));
                
        if (participantRepository.existsByUserAndHackathon(participant, hackathon)) {
            throw new ValidationException("You are already participating in this hackathon");
        }
        
        Participants p = Participants.builder()
                .user(participant)
                .hackathon(hackathon)
                .joinedAt(LocalDateTime.now())
                .build();
                
        participantRepository.save(p);
    }
    
    private HackathonDTO.Response mapToDTO(Hackathon hackathon) {
        applyAutoStatus(hackathon);
        HackathonDTO.Response.OrganizerInfo orgInfo = new HackathonDTO.Response.OrganizerInfo(
                hackathon.getOrganizer().getId(),
                hackathon.getOrganizer().getName(),
                hackathon.getOrganizer().getEmail()
        );
        
        long participants = participantRepository.countByHackathon(hackathon);
        long teams = teamRepository.countByHackathon(hackathon);

        return new HackathonDTO.Response(
                hackathon.getId(),
                hackathon.getTitle(),
                hackathon.getDescription(),
                hackathon.getPrizePool(),
                hackathon.getStartDate(),
                hackathon.getEndDate(),
                hackathon.getRegistrationDeadline(),
                hackathon.getMaxTeamSize(),
                hackathon.getStatus(),
                hackathon.getCreatedAt(),
                orgInfo,
                hackathon.getShortDescription(),
                hackathon.getType(),
                hackathon.getThemes(),
                hackathon.getProblemStatement(),
                hackathon.getGoals(),
                hackathon.getRules(),
                (int) participants,
                (int) teams
        );
    }

    private void applyAutoStatus(Hackathon hackathon) {
        LocalDate now = LocalDate.now();
        if (hackathon.getStartDate() != null && now.isBefore(hackathon.getStartDate())) {
            hackathon.setStatus("UPCOMING");
        } else if (hackathon.getEndDate() != null && now.isAfter(hackathon.getEndDate())) {
            hackathon.setStatus("COMPLETED");
        } else {
            hackathon.setStatus("ACTIVE");
        }
    }
}

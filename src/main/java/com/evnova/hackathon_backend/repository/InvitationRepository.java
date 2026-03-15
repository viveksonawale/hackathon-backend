package com.evnova.hackathon_backend.repository;

import com.evnova.hackathon_backend.model.Invitation;
import com.evnova.hackathon_backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByTeamAndEmail(Team team, String email);
    boolean existsByTeamAndEmail(Team team, String email);
}

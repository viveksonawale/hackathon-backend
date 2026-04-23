package com.evnova.hackathon_backend.repository;

import com.evnova.hackathon_backend.model.Invitation;
import com.evnova.hackathon_backend.model.Team;
import com.evnova.hackathon_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByTeamAndInvitedUser(Team team, User invitedUser);
    boolean existsByTeamAndInvitedUserAndStatus(Team team, User invitedUser, String status);
    List<Invitation> findByInvitedUserOrderByCreatedAtDesc(User invitedUser);
}

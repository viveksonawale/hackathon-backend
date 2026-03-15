package com.evnova.hackathon_backend.repository;

import com.evnova.hackathon_backend.model.Hackathon;
import com.evnova.hackathon_backend.model.Participants;
import com.evnova.hackathon_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participants, Long> {
    boolean existsByUserAndHackathon(User user, Hackathon hackathon);
    Optional<Participants> findByUserAndHackathon(User user, Hackathon hackathon);
    long countByHackathon(Hackathon hackathon);
    long countByHackathonIn(List<Hackathon> hackathons);
    List<Participants> findByUser(User user);
}

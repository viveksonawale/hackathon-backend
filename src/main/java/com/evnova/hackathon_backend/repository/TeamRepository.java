package com.evnova.hackathon_backend.repository;

import com.evnova.hackathon_backend.model.Hackathon;
import com.evnova.hackathon_backend.model.Team;
import com.evnova.hackathon_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByHackathonAndLeader(Hackathon hackathon, User leader);
    List<Team> findByLeader(User leader);
    List<Team> findByHackathon(Hackathon hackathon);
    boolean existsByHackathonAndLeader(Hackathon hackathon, User leader);
    long countByHackathon(Hackathon hackathon);
}

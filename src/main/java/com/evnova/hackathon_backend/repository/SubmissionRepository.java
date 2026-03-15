package com.evnova.hackathon_backend.repository;

import com.evnova.hackathon_backend.model.Hackathon;
import com.evnova.hackathon_backend.model.Submission;
import com.evnova.hackathon_backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByTeamAndHackathon(Team team, Hackathon hackathon);
    Optional<Submission> findByTeam(Team team);
    List<Submission> findByHackathon(Hackathon hackathon);
    List<Submission> findByHackathonOrderByScoreDesc(Hackathon hackathon);
    long countByHackathon(Hackathon hackathon);
    long countByHackathonIn(List<Hackathon> hackathons);
}

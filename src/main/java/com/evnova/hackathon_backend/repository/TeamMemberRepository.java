package com.evnova.hackathon_backend.repository;

import com.evnova.hackathon_backend.model.Team;
import com.evnova.hackathon_backend.model.TeamMember;
import com.evnova.hackathon_backend.model.User;
import com.evnova.hackathon_backend.model.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByTeam(Team team);
    Optional<TeamMember> findByTeamAndUser(Team team, User user);
    boolean existsByTeamAndUser(Team team, User user);
    boolean existsByUserAndTeam_Hackathon(User user, Hackathon hackathon);
    Optional<TeamMember> findFirstByUserAndTeam_Hackathon(User user, Hackathon hackathon);
    void deleteByTeamAndUser(Team team, User user);
}

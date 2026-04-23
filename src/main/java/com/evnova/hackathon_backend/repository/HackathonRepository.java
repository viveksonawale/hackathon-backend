package com.evnova.hackathon_backend.repository;

import com.evnova.hackathon_backend.model.Hackathon;
import com.evnova.hackathon_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {

    List<Hackathon> findByOrganizer(User organizer);

    List<Hackathon> findByStatus(String status);

    @Query("SELECT h FROM Hackathon h WHERE " +
            "(cast(:search as string) IS NULL OR LOWER(h.title) LIKE LOWER(CONCAT('%', cast(:search as string), '%')) " +
            "OR LOWER(h.description) LIKE LOWER(CONCAT('%', cast(:search as string), '%'))) AND " +
            "(cast(:status as string) IS NULL OR UPPER(h.status) = UPPER(cast(:status as string)))")
    List<Hackathon> findBySearchAndStatus(@Param("search") String search, @Param("status") String status);

    long countByOrganizer(User organizer);
}

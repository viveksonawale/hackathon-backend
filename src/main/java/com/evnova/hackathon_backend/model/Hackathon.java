package com.evnova.hackathon_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hackathons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hackathon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 500)
    private String shortDescription;

    @Column(length = 2000)
    private String description;

    private String type; // online/offline
    
    @ElementCollection
    private List<String> themes;

    private Double prizePool;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationDeadline;

    private Integer maxTeamSize;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    private String status;
    
    @Column(length = 2000)
    private String problemStatement;
    
    @ElementCollection
    private List<String> goals;
    
    @ElementCollection
    private List<String> rules;

    private LocalDateTime createdAt;
}

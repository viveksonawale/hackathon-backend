package com.evnova.hackathon_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class HackathonDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank
        private String title;

        private String description;

        private Double prizePool;

        @NotNull
        private LocalDate startDate;

        @NotNull
        private LocalDate endDate;

        private LocalDate registrationDeadline;

        private Integer maxTeamSize;

        private String status;

        private String shortDescription;
        private String type;
        private List<String> themes;
        private String problemStatement;
        private List<String> goals;
        private List<String> rules;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private Double prizePool;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDate registrationDeadline;
        private Integer maxTeamSize;
        private String status;
        private LocalDateTime createdAt;
        private OrganizerInfo organizer;
        
        private String shortDescription;
        private String type;
        private List<String> themes;
        private String problemStatement;
        private List<String> goals;
        private List<String> rules;
        private Integer participants;
        private Integer teams;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class OrganizerInfo {
            private Long id;
            private String name;
            private String email;
        }
    }
}

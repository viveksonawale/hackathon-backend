package com.evnova.hackathon_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class SubmissionDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank
        private String projectName;

        private String description;

        private String githubUrl;
        private String demoUrl;
        private String presentationUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long teamId;
        private String teamName;
        private Long hackathonId;
        private String projectName;
        private String description;
        private String githubUrl;
        private String demoUrl;
        private String presentationUrl;
        private LocalDateTime submittedAt;
        private Double score;
    }
}

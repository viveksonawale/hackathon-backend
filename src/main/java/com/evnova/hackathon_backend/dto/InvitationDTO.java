package com.evnova.hackathon_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class InvitationDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String status;
        private LocalDateTime createdAt;
        private Long teamId;
        private String teamName;
        private Long hackathonId;
        private String hackathonTitle;
        private UserBasic invitedBy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBasic {
        private Long id;
        private String name;
        private String email;
    }
}

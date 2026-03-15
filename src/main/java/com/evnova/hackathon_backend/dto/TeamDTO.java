package com.evnova.hackathon_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class TeamDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private Long hackathonId;
        private LocalDateTime createdAt;
        private LeaderInfo leader;
        private List<MemberInfo> members;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class LeaderInfo {
            private Long id;
            private String name;
            private String email;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class MemberInfo {
            private Long id;
            private String name;
            private String email;
            private String role;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InviteRequest {
        @NotBlank
        private String email;
    }
}

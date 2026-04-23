package com.evnova.hackathon_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class LeaderboardDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {
        private Integer rank;
        private Long teamId;
        private String teamName;
        private Double score;
        private List<TeamDTO.Response.MemberInfo> members;
    }
}

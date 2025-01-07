package com.meli.football_matches_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matchId")
    private Long matchId;

    @Column(name = "homeTeam", length = 100, nullable = true)
    private String homeTeam;

    @Column(name = "awayTeam", length = 100, nullable = true)
    private String awayTeam;

    @Column(name = "homeTeamId")
    private Long homeTeamId;

    @Column(name = "awayTeamId")
    private Long awayTeamId;

    @Column(name = "homeGoals", nullable = true)
    private int homeGoals;

    @Column(name = "awayGoals", nullable = true)
    private int awayGoals;

    @Column(name = "stadium", nullable = true)
    private String stadium;

    @Column(name = "matchDateTime", nullable = true)
    private LocalDateTime matchDateTime;

    public Long getMatchId() {
        return matchId;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Long getHomeTeamId() {
        return homeTeamId;
    }

    public Long getAwayTeamId() {
        return awayTeamId;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(int homeGoals) {
        this.homeGoals = homeGoals;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(int awayGoals) {
        this.awayGoals = awayGoals;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public LocalDateTime getMatchDateTime() {
        return matchDateTime;
    }

    public void setMatchDateTime(LocalDateTime matchDateTime) {
        this.matchDateTime = matchDateTime;
    }
}

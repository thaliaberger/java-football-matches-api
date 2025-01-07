package com.meli.football_matches_api.DTO;

import com.meli.football_matches_api.model.Match;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class MatchDTO {

    private Long matchId;
    private String homeTeam;
    private String awayTeam;
    private Long homeTeamId;
    private Long awayTeamId;
    private int homeGoals;
    private int awayGoals;
    private String stadium;
    private LocalDateTime matchDateTime;

    public MatchDTO() {}

    public MatchDTO(Match match) {
        BeanUtils.copyProperties(match, this);
    }

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

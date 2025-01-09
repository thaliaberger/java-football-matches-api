package com.meli.football_matches_api.DTO;

import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Team;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class MatchDTO {

    private Long id;
    private Integer homeGoals;
    private Integer awayGoals;
    private Long idStadium;
    private LocalDateTime matchDateTime;
    private Team homeTeam;
    private Team awayTeam;

    public MatchDTO() {}

    public MatchDTO(Match match) {
        BeanUtils.copyProperties(match, this);
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(int homeGoals) {
        this.homeGoals = homeGoals;
    }

    public Integer getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(int awayGoals) {
        this.awayGoals = awayGoals;
    }

    public Long getIdStadium() {
        return idStadium;
    }

    public void setIdStadium(Long idStadium) {
        this.idStadium = idStadium;
    }

    public void setAwayGoals(Integer awayGoals) {
        this.awayGoals = awayGoals;
    }

    public void setHomeGoals(Integer homeGoals) {
        this.homeGoals = homeGoals;
    }

    public LocalDateTime getMatchDateTime() {
        return matchDateTime;
    }

    public void setMatchDateTime(LocalDateTime matchDateTime) {
        this.matchDateTime = matchDateTime;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }
}

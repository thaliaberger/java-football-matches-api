package com.meli.football_matches_api.DTO;

import com.meli.football_matches_api.model.Match;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class MatchDTO {

    private Long id;
    private Long idHomeTeam;
    private Long idAwayTeam;
    private Integer homeGoals;
    private Integer awayGoals;
    private Long idStadium;
    private LocalDateTime matchDateTime;

    public MatchDTO() {}

    public MatchDTO(Match match) {
        BeanUtils.copyProperties(match, this);
    }
    public Long getId() {
        return id;
    }

    public Long getIdHomeTeam() {
        return idHomeTeam;
    }

    public void setIdHomeTeam(Long idHomeTeam) {
        this.idHomeTeam = idHomeTeam;
    }

    public Long getIdAwayTeam() {
        return idAwayTeam;
    }

    public void setIdAwayTeam(Long idAwayTeam) {
        this.idAwayTeam = idAwayTeam;
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
}

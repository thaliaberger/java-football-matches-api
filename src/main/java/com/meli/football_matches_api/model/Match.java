package com.meli.football_matches_api.model;

import com.meli.football_matches_api.DTO.MatchDTO;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_")

public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "idHomeTeam", nullable = true)
    private Long idHomeTeam;

    @Column(name = "idAwayTeam", nullable = true)
    private Long idAwayTeam;

    @Column(name = "homeGoals", nullable = true)
    private Integer homeGoals;

    @Column(name = "awayGoals", nullable = true)
    private Integer awayGoals;

    @Column(name = "estadio", length = 60, nullable = true)
    private String estadio;

    @Column(name = "matchDateTime", nullable = true)
    private LocalDateTime matchDateTime;

    public Match() {

    }

    public Match(MatchDTO matchDTO) {
        BeanUtils.copyProperties(matchDTO, this);
    }

    public Long getId() {
        return id;
    }

    public String getEstadio() {
        return estadio;
    }

    public void setEstadio(String estadio) {
        this.estadio = estadio;
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

    public LocalDateTime getMatchDateTime() {
        return matchDateTime;
    }

    public void setMatchDateTime(LocalDateTime matchDateTime) {
        this.matchDateTime = matchDateTime;
    }

    public Integer getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(Integer awayGoals) {
        this.awayGoals = awayGoals;
    }

    public Integer getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(Integer homeGoals) {
        this.homeGoals = homeGoals;
    }

}

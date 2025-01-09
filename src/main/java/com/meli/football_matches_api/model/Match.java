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

    @Column(name = "homeGoals", nullable = true)
    private Integer homeGoals;

    @Column(name = "awayGoals", nullable = true)
    private Integer awayGoals;

    @Column(name = "idStadium", nullable = true)
    private Long idStadium;

    @Column(name = "matchDateTime", nullable = true)
    private LocalDateTime matchDateTime;

    @OneToOne
    @JoinColumn(name = "fk_home_team")
    private Team homeTeam;

    @OneToOne
    @JoinColumn(name = "fk_away_team")
    private Team awayTeam;

    public Match() {

    }

    public Match(MatchDTO matchDTO) {
        BeanUtils.copyProperties(matchDTO, this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdStadium() {
        return idStadium;
    }

    public void setIdStadium(Long idStadium) {
        this.idStadium = idStadium;
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

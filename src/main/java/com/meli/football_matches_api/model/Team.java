package com.meli.football_matches_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meli.football_matches_api.DTO.RetrospectDTO;
import com.meli.football_matches_api.DTO.TeamDTO;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Entity
@Table
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "state", length = 2, nullable = false)
    private String state;

    @Column(name = "dateCreated", nullable = false)
    private LocalDate dateCreated;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive;

    @JsonIgnore
    @OneToMany(mappedBy = "homeTeam")
    private List<Match> homeMatches;

    @JsonIgnore
    @OneToMany(mappedBy = "awayTeam")
    private List<Match> awayMatches;

    public Team() {

    }

    public Team(TeamDTO teamDTO) {
        BeanUtils.copyProperties(teamDTO, this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<Match> getHomeMatches() {
        return homeMatches;
    }

    public void setHomeMatches(List<Match> homeMatches) {
        this.homeMatches = homeMatches;
    }

    public List<Match> getAwayMatches() {
        return awayMatches;
    }

    public void setAwayMatches(List<Match> awayMatches) {
        this.awayMatches = awayMatches;
    }

    public Integer getNumberOfMatches() {
        return getNumberOfMatches(homeMatches, awayMatches);
    }

    public Integer getNumberOfHomeMatches() {
        return getNumberOfMatches(homeMatches, null);
    }

    public Integer getNumberOfAwayMatches() {
        return getNumberOfMatches(null, awayMatches);
    }

    private Integer getNumberOfMatches(List<Match> homeMatches, List<Match> awayMatches) {
        int numberOfMatches = 0;

        if (homeMatches != null) numberOfMatches += homeMatches.size();
        if (awayMatches != null) numberOfMatches += awayMatches.size();

        return numberOfMatches;
    }

    public Integer getAllScoredGoals() {
        return getScoredGoals(homeMatches, awayMatches);
    }

    public Integer getHomeScoredGoals() {
        return getScoredGoals(homeMatches, null);
    }

    public Integer getAwayScoredGoals() {
        return getScoredGoals(null, awayMatches);
    }

    public Integer getScoredGoals(List<Match> homeMatches, List<Match> awayMatches) {
        return Stream.of(homeMatches, awayMatches)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .mapToInt(match -> match.getHomeTeam().getId().equals(getId()) ? match.getHomeGoals() : match.getAwayGoals())
                .sum();
    }

    public Integer getWins() {
        return getWins(homeMatches, awayMatches);
    }

    public Integer getHomeWins() {
        return getWins(homeMatches, null);
    }

    public Integer getAwayWins() {
        return getWins(null, awayMatches);
    }

    private Integer getWins(List<Match> homeMatches, List<Match> awayMatches) {
        RetrospectDTO retrospectDTO = new RetrospectDTO(homeMatches, awayMatches);
        return retrospectDTO.getWins();
    }

    public Integer getScore() {
        return getScore(homeMatches, awayMatches);
    }

    public Integer getScoreFromHomeMatches() {
        return getScore(homeMatches, null);
    }

    public Integer getScoreFromAwayMatches() {
        return getScore(null, awayMatches);
    }

    private Integer getScore(List<Match> homeMatches, List<Match> awayMatches) {
        RetrospectDTO retrospectDTO = new RetrospectDTO(homeMatches, awayMatches);
        return retrospectDTO.getScore();
    }
}

package com.meli.football_matches_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meli.football_matches_api.DTO.TeamDTO;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.List;

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

    public void setIsActive(Boolean active) {
        this.isActive = active;
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
}

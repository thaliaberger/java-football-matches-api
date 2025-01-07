package com.meli.football_matches_api.DTO;

import com.meli.football_matches_api.model.Team;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

public class TeamDTO {

    private Integer id;

    private String name;

    private String state;

    private LocalDate dateCreated;

    private Boolean isActive;

    private Integer wins;

    private Integer losses;

    private Integer draws;

    private Integer scoredGoals;

    private Integer concededGoals;

    public TeamDTO() {}

    public TeamDTO(Team team) {
        BeanUtils.copyProperties(team, this);
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

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }

    public Integer getDraws() {
        return draws;
    }

    public void setDraws(Integer draws) {
        this.draws = draws;
    }

    public Integer getScoredGoals() {
        return scoredGoals;
    }

    public void setScoredGoals(Integer scoredGoals) {
        this.scoredGoals = scoredGoals;
    }

    public Integer getConcededGoals() {
        return concededGoals;
    }

    public void setConcededGoals(Integer concededGoals) {
        this.concededGoals = concededGoals;
    }
}

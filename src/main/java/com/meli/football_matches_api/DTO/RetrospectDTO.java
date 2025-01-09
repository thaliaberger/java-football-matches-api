package com.meli.football_matches_api.DTO;

import com.meli.football_matches_api.model.Team;
import org.springframework.beans.BeanUtils;

public class RetrospectDTO {

    private Integer wins = 0;

    private Integer losses = 0;

    private Integer draws = 0;

    private Integer scoredGoals = 0;

    private Integer concededGoals = 0;

    public RetrospectDTO() {}

    public RetrospectDTO(Team team) {
        BeanUtils.copyProperties(team, this);
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

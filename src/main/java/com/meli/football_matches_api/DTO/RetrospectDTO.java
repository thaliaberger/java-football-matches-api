package com.meli.football_matches_api.DTO;

import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Team;
import org.springframework.beans.BeanUtils;

import java.util.List;

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

    public RetrospectDTO(List<Match> homeMatches, List<Match> awayMatches) {
        processMatches(homeMatches, true);
        processMatches(awayMatches, false);
    }

    private void processMatches(List<Match> matches, boolean isHomeMatch) {
        int wins = 0;
        int losses = 0;
        int draws = 0;
        int scoredGoals = 0;
        int concededGoals = 0;

        for (Match match : matches) {
            int homeGoals = match.getHomeGoals();
            int awayGoals = match.getAwayGoals();

            int teamScoredGoals = isHomeMatch ? homeGoals : awayGoals;
            int teamConcededGoals = isHomeMatch ? awayGoals : homeGoals;

            if (teamScoredGoals > teamConcededGoals) {
                wins++;
            } else if (teamScoredGoals < teamConcededGoals) {
                losses++;
            } else {
                draws++;
            }

            scoredGoals += teamScoredGoals;
            concededGoals += teamConcededGoals;
        }

        setWins(getWins() + wins);
        setLosses(getLosses() + losses);
        setDraws(getDraws() + draws);
        setScoredGoals(getScoredGoals() + scoredGoals);
        setConcededGoals(getConcededGoals() + concededGoals);
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
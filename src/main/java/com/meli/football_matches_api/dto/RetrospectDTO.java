package com.meli.football_matches_api.dto;

import com.meli.football_matches_api.model.Match;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class RetrospectDTO {

    private Integer wins = 0;
    private Integer losses = 0;
    private Integer draws = 0;
    private Integer scoredGoals = 0;
    private Integer concededGoals = 0;
    private Integer score = 0;
    private List<Match> matches;

    public RetrospectDTO(Match match, Boolean isHomeMatch) {
        processMatch(match, isHomeMatch);
    }

    public RetrospectDTO(List<Match> homeMatches, List<Match> awayMatches) {
        if (homeMatches != null) homeMatches.forEach(match -> processMatch(match, true));
        if (awayMatches != null) awayMatches.forEach(match -> processMatch(match, false));

        List<Match> allMatches = Stream.of(homeMatches, awayMatches)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .toList();

        setMatches(allMatches);
    }

    private void processMatch(Match match, boolean isHomeMatch) {
        int homeGoals = match.getHomeGoals();
        int awayGoals = match.getAwayGoals();

        int teamScoredGoals = isHomeMatch ? homeGoals : awayGoals;
        int teamConcededGoals = isHomeMatch ? awayGoals : homeGoals;

        if (teamScoredGoals > teamConcededGoals) {
            wins++;
            score += 3;
        } else if (teamScoredGoals < teamConcededGoals) {
            losses++;
        } else {
            draws++;
            score++;
        }

        scoredGoals += teamScoredGoals;
        concededGoals += teamConcededGoals;
    }

    public static RetrospectDTO update(RetrospectDTO retrospectDTO, Match match, Boolean isHomeMatch) {
        retrospectDTO.processMatch(match, isHomeMatch);
        return retrospectDTO;
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

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}

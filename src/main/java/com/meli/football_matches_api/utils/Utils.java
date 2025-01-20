package com.meli.football_matches_api.utils;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.DTO.StadiumDTO;
import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.TeamRepository;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Utils {

    public static List<TeamDTO> convertToTeamDTO(List<Team> teams) {
        List<TeamDTO> teamDTOs = new ArrayList<>();

        for (Team team : teams) {
            TeamDTO teamDTO = new TeamDTO(team);
            teamDTOs.add(teamDTO);
        }

        return teamDTOs;
    }

    public static List<MatchDTO> convertToMatchDTO(List<Match> matches) {
        List<MatchDTO> matchDTOS = new ArrayList<>();

        for (Match match : matches) {
            MatchDTO matchDTO = new MatchDTO(match);
            matchDTOS.add(matchDTO);
        }

        return matchDTOS;
    }

    public static List<StadiumDTO> convertToStadiumDTO(List<Stadium> stadiums) {
        List<StadiumDTO> stadiumDTOS = new ArrayList<>();

        for (Stadium stadium : stadiums) {
            StadiumDTO stadiumDTO = new StadiumDTO(stadium);
            stadiumDTOS.add(stadiumDTO);
        }

        return stadiumDTOS;
    }

    public static Sort handleSortParams(String sort) {
        String[] sortParams = sort.split(",");
        return sortParams[1].equalsIgnoreCase("asc") ? Sort.by(sortParams[0]).ascending() : Sort.by(sortParams[0]).descending();
    }

    public static TeamFilter getFilter(String rankBy) {
        switch (rankBy) {
            case "wins":
                return filterByWins();
            case "goals":
                return filterByScoredGoals();
            case "score":
                return filterByScore();
            default:
                return null;
        }
    }

    public static Team getTeamById(TeamRepository repository, Long id, boolean isOpponent) {
        Team team = repository.findById(id);
        if (team == null) {
            throw new NotFoundException(isOpponent ? "Opponent team not found" : "Team not found");
        }
        return team;
    }

    public static List<Match> getHammeringMatches(List<Match> matches) {
        List<Match> hammeringMatches = new ArrayList<>();

        for (Match match : matches) {
            if (match.isHammering()) hammeringMatches.add(match);
        }

        return hammeringMatches;
    }

    public static List<Match> filterByOpponent(List<Match> matches, Long opponentId, boolean isHomeMatch) {
        return matches.stream().filter(match -> Objects.equals(isHomeMatch ? match.getAwayTeam().getId() : match.getHomeTeam().getId(), opponentId)).collect(Collectors.toList());
    }

    private static TeamFilter filterByWins() {
        return team -> team.getWins() != 0;
    }

    private static TeamFilter filterByScoredGoals() { return team -> team.getAllScoredGoals() != 0; }

    private static TeamFilter filterByScore() {
        return team -> team.getScore() != 0;
    }
}

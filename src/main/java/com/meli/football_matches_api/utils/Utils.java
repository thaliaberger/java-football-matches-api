package com.meli.football_matches_api.utils;

import com.meli.football_matches_api.dto.MatchDTO;
import com.meli.football_matches_api.dto.RetrospectDTO;
import com.meli.football_matches_api.dto.StadiumDTO;
import com.meli.football_matches_api.dto.TeamDTO;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.model.Team;
import org.springframework.data.domain.Sort;

import java.util.*;

public class Utils {

    private Utils() {}

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

    public static List<Match> getHammeringMatches(List<Match> matches) {
        List<Match> hammeringMatches = new ArrayList<>();

        for (Match match : matches) {
            if (match.isHammering()) hammeringMatches.add(match);
        }

        return hammeringMatches;
    }

    public static List<Match> filterByOpponent(List<Match> matches, Long opponentId, boolean isHomeMatch) {
        if (matches == null) return List.of();
        return matches.stream().filter(match -> Objects.equals(isHomeMatch ? match.getAwayTeam().getId() : match.getHomeTeam().getId(), opponentId)).toList();
    }

    public static Comparator<TeamDTO> getComparator(String rankBy, String matchLocation) {
        switch (rankBy) {
            case "matches":
                return getComparatorByMatches(matchLocation);
            case "wins":
                return getComparatorByWins(matchLocation);
            case "goals":
                return getComparatorByGoals(matchLocation);
            case "score":
                return getComparatorByScore(matchLocation);
            default:
                throw new IllegalArgumentException("Invalid rank type: " + rankBy);
        }
    }

    private static Comparator<TeamDTO> getComparatorByMatches(String matchLocation) {
        if (matchLocation == null || matchLocation.isEmpty()) return Comparator.comparing(TeamDTO::getNumberOfMatches).reversed();
        if (matchLocation.equals("home")) return Comparator.comparing(TeamDTO::getNumberOfHomeMatches).reversed();
        return Comparator.comparing(TeamDTO::getNumberOfAwayMatches).reversed();
    }

    private static Comparator<TeamDTO> getComparatorByWins(String matchLocation) {
        if (matchLocation == null || matchLocation.isEmpty()) return Comparator.comparing(TeamDTO::getWins).reversed();
        if (matchLocation.equals("home")) return Comparator.comparing(TeamDTO::getHomeWins).reversed();
        return Comparator.comparing(TeamDTO::getAwayWins).reversed();
    }

    private static Comparator<TeamDTO> getComparatorByGoals(String matchLocation) {
        if (matchLocation == null || matchLocation.isEmpty()) return Comparator.comparing(TeamDTO::getAllScoredGoals).reversed();
        if (matchLocation.equals("home")) return Comparator.comparing(TeamDTO::getHomeScoredGoals).reversed();
        return Comparator.comparing(TeamDTO::getAwayScoredGoals).reversed();
    }

    private static Comparator<TeamDTO> getComparatorByScore(String matchLocation) {
        if (matchLocation == null || matchLocation.isEmpty()) return Comparator.comparing(TeamDTO::getScore).reversed();
        if (matchLocation.equals("home")) return Comparator.comparing(TeamDTO::getScoreFromHomeMatches).reversed();
        return Comparator.comparing(TeamDTO::getScoreFromAwayMatches).reversed();
    }

    public static RetrospectDTO createRetrospectDTO(Team team, Long opponentId, String matchLocation, boolean isHammering) {
        List<Match> homeMatches = opponentId == null ? team.getHomeMatches() : Utils.filterByOpponent(team.getHomeMatches(), opponentId, true);
        List<Match> awayMatches = opponentId == null ? team.getAwayMatches() : Utils.filterByOpponent(team.getAwayMatches(), opponentId, false);

        if (isHammering) {
            if (homeMatches != null) homeMatches = homeMatches.stream().filter(Match::isHammering).toList();
            if (awayMatches != null) awayMatches = awayMatches.stream().filter(Match::isHammering).toList();
        }

        return createRetrospectDTOByLocation(homeMatches, awayMatches, matchLocation);
    }

    public static RetrospectDTO createRetrospectDTOByLocation(List<Match> homeMatches, List<Match> awayMatches, String matchLocation) {
        if (matchLocation == null || matchLocation.isEmpty()) {
            return new RetrospectDTO(homeMatches, awayMatches);
        } else if (matchLocation.equals("home")) {
            return new RetrospectDTO(homeMatches, null);
        } else {
            return new RetrospectDTO(null, awayMatches);
        }
    }

    public static List<TeamDTO> rankTeams(List<TeamDTO> teams, Comparator<TeamDTO> comparator, TeamFilter filter) {
        List<TeamDTO> filteredTeams = new ArrayList<>();

        for (TeamDTO team : teams) {
            if (filter == null || filter.filter(team)) filteredTeams.add(team);
        }

        filteredTeams.sort(comparator);

        return filteredTeams;
    }

    public static void populateRetrospectsByOpponentHashMap(List<Match> matches, boolean isHomeMatch, Map<String, RetrospectDTO> retrospectsByOpponent) {
        for (Match match : matches) {
            String currentOpponent = isHomeMatch ? match.getAwayTeam().getName() : match.getHomeTeam().getName();

            RetrospectDTO newDTO;

            if (retrospectsByOpponent.containsKey(currentOpponent)) {
                newDTO = RetrospectDTO.update(retrospectsByOpponent.get(currentOpponent), match, isHomeMatch);
                retrospectsByOpponent.put(currentOpponent, newDTO);
            } else {
                newDTO = new RetrospectDTO(match, isHomeMatch);
                retrospectsByOpponent.put(currentOpponent, newDTO);
            }

            retrospectsByOpponent.put(currentOpponent, newDTO);
        }
    }

    private static TeamFilter filterByWins() {
        return team -> team.getWins() != 0;
    }

    private static TeamFilter filterByScoredGoals() { return team -> team.getAllScoredGoals() != 0; }

    private static TeamFilter filterByScore() {
        return team -> team.getScore() != 0;
    }
}

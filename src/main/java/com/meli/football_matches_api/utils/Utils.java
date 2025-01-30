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
}

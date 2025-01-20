package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.RetrospectDTO;
import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.utils.TeamFilter;
import com.meli.football_matches_api.utils.Utils;
import com.meli.football_matches_api.validations.TeamValidations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeamService {

    private final TeamRepository repository;

    public TeamService(TeamRepository repository) {
        this.repository = repository;
    };

    public ResponseEntity<TeamDTO> create(TeamDTO teamDTO) {
        return saveTeam(teamDTO, false, HttpStatus.CREATED);
    }

    public ResponseEntity<TeamDTO> update(TeamDTO teamDTO) {
        return saveTeam(teamDTO, true, HttpStatus.OK);
    }

    private ResponseEntity<TeamDTO> saveTeam(TeamDTO teamDTO, Boolean isUpdate, HttpStatus status) {
        TeamValidations.validateFields(teamDTO, repository, isUpdate);

        Team team = new Team(teamDTO);
        TeamDTO savedTeam = new TeamDTO(repository.save(team));
        return ResponseEntity.status(status).body(savedTeam);
    }

    public ResponseEntity<TeamDTO> get(Long id) {
        Team team = repository.findById(id);
        if (team == null) throw new NotFoundException("Team not found");

        TeamDTO teamDTO = new TeamDTO(team);
        return ResponseEntity.status(HttpStatus.OK).body(teamDTO);
    }

    public ResponseEntity<List<TeamDTO>> list(int page, int itemsPerPage, String sort) {
        return list(page, itemsPerPage, sort, null, null, null);
    }

    public ResponseEntity<List<TeamDTO>> list(int page, int itemsPerPage, String sort, Boolean isActive) {
        return list(page, itemsPerPage, sort, isActive, null, null);
    }

    public ResponseEntity<List<TeamDTO>> list(int page, int itemsPerPage, String sort, String param, Boolean isNameSearch) {
        return list(page, itemsPerPage, sort, null, param, isNameSearch);
    }

    public ResponseEntity<List<TeamDTO>> list(
        int page, int itemsPerPage, String sort, Boolean isActive,
        String param, Boolean isNameSearch) {

        Pageable pageable = PageRequest.of(page, itemsPerPage, Utils.handleSortParams(sort));
        List<Team> teams;

        if (isActive != null) {
            teams = repository.findAllByIsActive(isActive, pageable);
        } else if (param != null) {
            if (isNameSearch != null && isNameSearch) {
                teams = repository.findAllByName(param, pageable);
            } else {
                teams = repository.findAllByState(param, pageable);
            }
        } else {
            teams = repository.findAll(pageable).getContent();
        }

        return ResponseEntity.status(HttpStatus.OK).body(Utils.convertToTeamDTO(teams));
    }

    public ResponseEntity<String> delete(Long id) {
        Team team = repository.findById(id);
        team.setIsActive(false);
        repository.save(team);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    public ResponseEntity<RetrospectDTO> getRetrospect(Long id, String matchLocation, boolean isHammering) {
        Team team = Utils.getTeamById(repository, id, false);
        return ResponseEntity.status(HttpStatus.OK).body(createRetrospectDTO(team, null, matchLocation, isHammering));
    }

    public ResponseEntity<RetrospectDTO> getRetrospect(Long id, Long opponentId, String matchLocation, boolean isHammering) {
        Team team = Utils.getTeamById(repository, id, false);
        Team opponentTeam = Utils.getTeamById(repository, opponentId, true);

        return ResponseEntity.status(HttpStatus.OK).body(createRetrospectDTO(team, opponentId, matchLocation, isHammering));
    }

    private RetrospectDTO createRetrospectDTO(Team team, Long opponentId, String matchLocation, boolean isHammering) {
        List<Match> homeMatches = opponentId == null ? team.getHomeMatches() : Utils.filterByOpponent(team.getHomeMatches(), opponentId, true);
        List<Match> awayMatches = opponentId == null ? team.getAwayMatches() : Utils.filterByOpponent(team.getAwayMatches(), opponentId, false);

        if (isHammering) {
            homeMatches = homeMatches.stream().filter(Match::isHammering).toList();
            awayMatches = awayMatches.stream().filter(Match::isHammering).toList();
        }

        return createRetrospectDTOByLocation(homeMatches, awayMatches, matchLocation);
    }

    private RetrospectDTO createRetrospectDTOByLocation(List<Match> homeMatches, List<Match> awayMatches, String matchLocation) {
        if (matchLocation == null || matchLocation.isEmpty()) {
            return new RetrospectDTO(homeMatches, awayMatches);
        } else if (matchLocation.equals("home")) {
            return new RetrospectDTO(homeMatches, null);
        } else {
            return new RetrospectDTO(null, awayMatches);
        }
    }

    public ResponseEntity<HashMap<String, RetrospectDTO>> getRetrospectAgainstAll(Long id) {
        Team team = repository.findById(id);
        if (team == null) throw new NotFoundException("Team not found");

        List<Match> homeMatches = team.getHomeMatches();
        List<Match> awayMatches = team.getAwayMatches();

        HashMap<String, RetrospectDTO> retrospectsByOpponent = new HashMap<>();

        processMatches(homeMatches, true, retrospectsByOpponent);
        processMatches(awayMatches, false, retrospectsByOpponent);

        return ResponseEntity.status(HttpStatus.OK).body(retrospectsByOpponent);
    }

    private void processMatches(List<Match> matches, boolean isHomeMatch, HashMap<String, RetrospectDTO> retrospectsByOpponent) {
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

    public ResponseEntity<List<TeamDTO>> ranking(String rankBy) {
        return ranking(rankBy, null);
    }

    public ResponseEntity<List<TeamDTO>> ranking(String rankBy, String matchLocation) {
        TeamFilter filter = Utils.getFilter(rankBy);
        Comparator<TeamDTO> comparator = getComparator(rankBy, matchLocation);
        List<Team> teams = getTeamsByMatchLocation(rankBy, matchLocation);
        return ResponseEntity.status(HttpStatus.OK).body(rankTeams(Utils.convertToTeamDTO(teams), comparator, filter));
    }

    private Comparator<TeamDTO> getComparator(String rankBy, String matchLocation) {
        switch (rankBy) {
            case "matches":
                if (matchLocation == null || matchLocation.isEmpty()) return Comparator.comparing(TeamDTO::getNumberOfMatches).reversed();
                if (matchLocation.equals("home")) return Comparator.comparing(TeamDTO::getNumberOfHomeMatches).reversed();
                return Comparator.comparing(TeamDTO::getNumberOfAwayMatches).reversed();
            case "wins":
                if (matchLocation == null || matchLocation.isEmpty()) return Comparator.comparing(TeamDTO::getWins).reversed();
                if (matchLocation.equals("home")) return Comparator.comparing(TeamDTO::getHomeWins).reversed();
                return Comparator.comparing(TeamDTO::getAwayWins).reversed();
            case "goals":
                if (matchLocation == null || matchLocation.isEmpty()) return Comparator.comparing(TeamDTO::getAllScoredGoals).reversed();
                if (matchLocation.equals("home")) return Comparator.comparing(TeamDTO::getHomeScoredGoals).reversed();
                return Comparator.comparing(TeamDTO::getAwayScoredGoals).reversed();
            case "score":
                if (matchLocation == null || matchLocation.isEmpty()) return Comparator.comparing(TeamDTO::getScore).reversed();
                if (matchLocation.equals("home")) return Comparator.comparing(TeamDTO::getScoreFromHomeMatches).reversed();
                return Comparator.comparing(TeamDTO::getScoreFromAwayMatches).reversed();
            default:
                throw new IllegalArgumentException("Invalid rank type: " + rankBy);
        }
    }

    private List<Team> getTeamsByMatchLocation(String rankBy, String matchLocation) {
        switch (rankBy) {
            case "matches":
            case "score":
                if ("home".equals(matchLocation)) {
                    return repository.findByHomeMatchesNotNull();
                } else if ("away".equals(matchLocation)) {
                    return repository.findByAwayMatchesNotNull();
                } else {
                    return repository.findByHomeMatchesNotNullOrAwayMatchesNotNull();
                }
            case "wins":
            case "goals":
                if ("home".equals(matchLocation)) {
                    return repository.findByHomeMatchesHomeGoalsNotNull();
                } else if ("away".equals(matchLocation)) {
                    return repository.findByAwayMatchesHomeGoalsNotNull();
                } else {
                    return repository.findByHomeMatchesNotNullOrAwayMatchesNotNull();
                }
            default:
                throw new IllegalArgumentException("Invalid ranking criteria: " + rankBy);
        }
    }

    public List<TeamDTO> rankTeams(List<TeamDTO> teams, Comparator<TeamDTO> comparator, TeamFilter filter) {
        List<TeamDTO> filteredTeams = new ArrayList<>();

        for (TeamDTO team : teams) {
            if (filter == null || filter.filter(team)) filteredTeams.add(team);
        }

        filteredTeams.sort(comparator);

        return filteredTeams;
    }
}

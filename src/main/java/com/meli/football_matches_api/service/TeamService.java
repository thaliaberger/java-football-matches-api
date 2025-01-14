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
        TeamValidations.validateFields(teamDTO, repository);
        TeamValidations.validateIfTeamAlreadyExists(teamDTO.getId(), teamDTO.getName(), teamDTO.getState(), repository);

        Team newTeam = new Team(teamDTO);
        TeamDTO savedTeam = new TeamDTO(repository.save(newTeam));
        return ResponseEntity.status(201).body(savedTeam);
    };

    public ResponseEntity<TeamDTO> update(TeamDTO teamDTO) {
        repository.findById(teamDTO.getId()).orElseThrow(() -> new NotFoundException("Team not found"));

        TeamValidations.validateFields(teamDTO, repository);
        TeamValidations.validateIfTeamAlreadyExists(teamDTO.getId(), teamDTO.getName(), teamDTO.getState(), repository);

        Team updatedTeam = new Team(teamDTO);
        TeamDTO savedTeam = new TeamDTO(repository.save(updatedTeam));
        return ResponseEntity.status(200).body(savedTeam);
    }

    public ResponseEntity<TeamDTO> get(int id) {
        Team team = repository.findById(id);
        if (team == null) throw new NotFoundException("Team not found");

        TeamDTO teamDTO = new TeamDTO(team);
        return ResponseEntity.status(200).body(teamDTO);
    }

    public ResponseEntity<List<TeamDTO>> list() {
        List<Team> teams = repository.findAll();
        return ResponseEntity.status(200).body(Utils.convertToDTO(teams));
    }

    public ResponseEntity<List<TeamDTO>> list(String sort) {
        return list(0, 1000, sort);
    }

    public ResponseEntity<List<TeamDTO>> list(int page, int itemsPerPage, String sort) {
        Pageable pageable = PageRequest.of(page, itemsPerPage, Utils.handleSortParams(sort));
        List<Team> teams = repository.findAll(pageable).getContent();
        return ResponseEntity.status(200).body(Utils.convertToDTO(teams));
    }

    public ResponseEntity<List<TeamDTO>> list(String param, Boolean isNameSearch) {
        List<Team> teams;

        if (isNameSearch) {
            teams = repository.findAllByName(param);
        } else {
            teams = repository.findAllByState(param);
        }
        return ResponseEntity.status(200).body(Utils.convertToDTO(teams));
    }

    public ResponseEntity<List<TeamDTO>> list(Boolean isActive) {
        List<Team> teams = repository.findAllByIsActive(isActive);
        return ResponseEntity.status(200).body(Utils.convertToDTO(teams));
    }

    public ResponseEntity<String> delete(int id) {
        Team team = repository.findById(id);
        team.setIsActive(false);
        repository.save(team);
        return ResponseEntity.status(204).body("");
    }

    public ResponseEntity<RetrospectDTO> getRetrospect(int id) {
        return ResponseEntity.ok(getRetrospectDTO(id, null));
    }

    public ResponseEntity<RetrospectDTO> getRetrospect(int id, String matchLocation) {
        return ResponseEntity.ok(getRetrospectDTO(id, matchLocation));
    }

    private RetrospectDTO getRetrospectDTO(int id, String matchLocation) {
        Team team = repository.findById(id);
        if (team == null) throw new NotFoundException("Team not found");

        if (matchLocation == null || matchLocation.isEmpty()) return new RetrospectDTO(team.getHomeMatches(), team.getAwayMatches());

        if (matchLocation.equals("home")) return new RetrospectDTO(team.getHomeMatches(), null);

        return new RetrospectDTO(null, team.getAwayMatches());
    }

    public ResponseEntity<RetrospectDTO> getRetrospect(int id, int opponentId) {
        Team team = repository.findById(id);
        if (team == null) throw new NotFoundException("Team not found");

        Team opponentTeam = repository.findById(opponentId);
        if (opponentTeam == null) throw new NotFoundException("Opponent team not found");

        List<Match> homeMatchesAgainstOpponent = team.getHomeMatches().stream().filter(match -> match.getAwayTeam().getId() == opponentId).toList();
        List<Match> awayMatchesAgainstOpponent = team.getAwayMatches().stream().filter(match -> match.getHomeTeam().getId() == opponentId).toList();

        RetrospectDTO retrospectDTO = new RetrospectDTO(homeMatchesAgainstOpponent, awayMatchesAgainstOpponent);
        return ResponseEntity.status(200).body(retrospectDTO);
    }

    public ResponseEntity<RetrospectDTO> getRetrospect(int id, int opponentId, boolean isHammering) {
        if (!isHammering) return getRetrospect(id, opponentId);

        Team team = repository.findById(id);
        if (team == null) throw new NotFoundException("Team not found");

        Team opponentTeam = repository.findById(opponentId);
        if (opponentTeam == null) throw new NotFoundException("Opponent team not found");

        List<Match> homeMatchesAgainstOpponent = team.getHomeMatches().stream().filter(match -> match.getAwayTeam().getId() == opponentId && match.isHammering()).toList();
        List<Match> awayMatchesAgainstOpponent = team.getAwayMatches().stream().filter(match -> match.getHomeTeam().getId() == opponentId && match.isHammering()).toList();

        RetrospectDTO retrospectDTO = new RetrospectDTO(homeMatchesAgainstOpponent, awayMatchesAgainstOpponent);
        return ResponseEntity.status(200).body(retrospectDTO);
    }

    public ResponseEntity<HashMap<String, RetrospectDTO>> getRetrospectAgainstAll(int id) {
        Team team = repository.findById(id);
        if (team == null) throw new NotFoundException("Team not found");

        List<Match> homeMatches = team.getHomeMatches();
        List<Match> awayMatches = team.getAwayMatches();

        HashMap<String, RetrospectDTO> retrospectsByOpponent = new HashMap<>();

        processMatches(homeMatches, true, retrospectsByOpponent);
        processMatches(awayMatches, false, retrospectsByOpponent);

        return ResponseEntity.status(200).body(retrospectsByOpponent);
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

    public ResponseEntity<PriorityQueue<Team>> ranking(String rankBy) {
        return ranking(rankBy, null);
    }

    public ResponseEntity<PriorityQueue<Team>> ranking(String rankBy, String matchLocation) {
        TeamFilter filter = getFilter(rankBy);
        Comparator<Team> comparator = getComparator(rankBy);
        List<Team> teams = getTeamsByMatchLocation(rankBy, matchLocation);

        PriorityQueue<Team> rankedTeams = buildPriorityQueue(teams, comparator, filter);

        return ResponseEntity.status(200).body(rankedTeams);
    }

    private TeamFilter getFilter(String rankBy) {
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

    private Comparator<Team> getComparator(String rankBy) {
        switch (rankBy) {
            case "matches":
                return Comparator.comparing(Team::getNumberOfMatches).reversed();
            case "wins":
                return Comparator.comparing(Team::getWins).reversed();
            case "goals":
                return Comparator.comparing(Team::getScoredGoals).reversed();
            case "score":
                return Comparator.comparing(Team::getScore).reversed();
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

    private PriorityQueue<Team> buildPriorityQueue(List<Team> teams, Comparator<Team> comparator, TeamFilter filter) {
        PriorityQueue<Team> maxHeap = new PriorityQueue<>(comparator);
        for (Team team : teams) {
            if (filter == null || filter.filter(team)) maxHeap.add(team);
        }

        return maxHeap;
    }

    public TeamFilter filterByWins() {
        return team -> team.getWins() != 0;
    }

    public TeamFilter filterByScoredGoals() {
        return team -> team.getScoredGoals() != 0;
    }

    public TeamFilter filterByScore() {
        return team -> team.getScore() != 0;
    }

}

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
        Team team = Utils.getTeamById(repository, id, false);
        team.setIsActive(false);
        repository.save(team);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    public ResponseEntity<RetrospectDTO> getRetrospect(Long id, String matchLocation, boolean isHammering) {
        Team team = Utils.getTeamById(repository, id, false);
        return ResponseEntity.status(HttpStatus.OK).body(Utils.createRetrospectDTO(team, null, matchLocation, isHammering));
    }

    public ResponseEntity<RetrospectDTO> getRetrospect(Long id, Long opponentId, String matchLocation, boolean isHammering) {
        Team team = Utils.getTeamById(repository, id, false);
        Team opponentTeam = Utils.getTeamById(repository, opponentId, true);

        return ResponseEntity.status(HttpStatus.OK).body(Utils.createRetrospectDTO(team, opponentId, matchLocation, isHammering));
    }

    public ResponseEntity<HashMap<String, RetrospectDTO>> getRetrospectAgainstAll(Long id) {
        Team team = repository.findById(id);
        if (team == null) throw new NotFoundException("Team not found");

        List<Match> homeMatches = team.getHomeMatches();
        List<Match> awayMatches = team.getAwayMatches();

        HashMap<String, RetrospectDTO> retrospectsByOpponent = new HashMap<>();

        Utils.populateRetrospectsByOpponentHashMap(homeMatches, true, retrospectsByOpponent);
        Utils.populateRetrospectsByOpponentHashMap(awayMatches, false, retrospectsByOpponent);

        return ResponseEntity.status(HttpStatus.OK).body(retrospectsByOpponent);
    }

    public ResponseEntity<List<TeamDTO>> ranking(String rankBy) {
        return ranking(rankBy, null);
    }

    public ResponseEntity<List<TeamDTO>> ranking(String rankBy, String matchLocation) {
        TeamFilter filter = Utils.getFilter(rankBy);
        Comparator<TeamDTO> comparator = Utils.getComparator(rankBy, matchLocation);
        List<Team> teams = Utils.getTeamsByMatchLocation(rankBy, matchLocation, repository);
        return ResponseEntity.status(HttpStatus.OK).body(Utils.rankTeams(Utils.convertToTeamDTO(teams), comparator, filter));
    }
}

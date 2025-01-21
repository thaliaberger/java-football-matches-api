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
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeamService {

    private final TeamRepository repository;

    public TeamService(TeamRepository repository) {
        this.repository = repository;
    };

    public TeamDTO create(TeamDTO teamDTO) {
        return saveTeam(teamDTO, false);
    }

    public TeamDTO update(TeamDTO teamDTO) {
        return saveTeam(teamDTO, true);
    }

    private TeamDTO saveTeam(TeamDTO teamDTO, Boolean isUpdate) {
        TeamValidations.validateFields(teamDTO, repository, isUpdate);
        Team team = new Team(teamDTO);
        return new TeamDTO(repository.save(team));
    }

    public TeamDTO get(Long id) {
        Team team = repository.findById(id);
        if (team == null) throw new NotFoundException("Team not found");
        return new TeamDTO(team);
    }

    public List<TeamDTO> list(int page, int itemsPerPage, String sort) {
        return list(page, itemsPerPage, sort, null, null, null);
    }

    public List<TeamDTO> list(int page, int itemsPerPage, String sort, Boolean isActive) {
        return list(page, itemsPerPage, sort, isActive, null, null);
    }

    public List<TeamDTO> list(int page, int itemsPerPage, String sort, String param, Boolean isNameSearch) {
        return list(page, itemsPerPage, sort, null, param, isNameSearch);
    }

    public List<TeamDTO> list(
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

        return Utils.convertToTeamDTO(teams);
    }

    public String delete(Long id) {
        Team team = Utils.getTeamById(repository, id, false);
        team.setIsActive(false);
        repository.save(team);
        return "";
    }

    public RetrospectDTO getRetrospect(Long id, String matchLocation, boolean isHammering) {
        Team team = Utils.getTeamById(repository, id, false);
        return Utils.createRetrospectDTO(team, null, matchLocation, isHammering);
    }

    public RetrospectDTO getRetrospect(Long id, Long opponentId, String matchLocation, boolean isHammering) {
        Team team = Utils.getTeamById(repository, id, false);
        Team opponentTeam = Utils.getTeamById(repository, opponentId, true);

        return Utils.createRetrospectDTO(team, opponentId, matchLocation, isHammering);
    }

    public HashMap<String, RetrospectDTO> getRetrospectAgainstAll(Long id) {
        Team team = repository.findById(id);
        if (team == null) throw new NotFoundException("Team not found");

        List<Match> homeMatches = team.getHomeMatches();
        List<Match> awayMatches = team.getAwayMatches();

        HashMap<String, RetrospectDTO> retrospectsByOpponent = new HashMap<>();

        Utils.populateRetrospectsByOpponentHashMap(homeMatches, true, retrospectsByOpponent);
        Utils.populateRetrospectsByOpponentHashMap(awayMatches, false, retrospectsByOpponent);

        return retrospectsByOpponent;
    }

    public List<TeamDTO> ranking(String rankBy) {
        return ranking(rankBy, null);
    }

    public List<TeamDTO> ranking(String rankBy, String matchLocation) {
        TeamFilter filter = Utils.getFilter(rankBy);
        Comparator<TeamDTO> comparator = Utils.getComparator(rankBy, matchLocation);
        List<Team> teams = Utils.getTeamsByMatchLocation(rankBy, matchLocation, repository);
        return Utils.rankTeams(Utils.convertToTeamDTO(teams), comparator, filter);
    }
}

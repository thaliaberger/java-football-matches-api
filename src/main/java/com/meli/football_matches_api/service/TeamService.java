package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.RetrospectDTO;
import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.specification.TeamSpecification;
import com.meli.football_matches_api.utils.TeamFilter;
import com.meli.football_matches_api.utils.Utils;
import com.meli.football_matches_api.validations.TeamValidations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public List<TeamDTO> list(int page, int itemsPerPage, String sort, String name, String state, Boolean isActive) {
        Pageable pageable = PageRequest.of(page, itemsPerPage, Utils.handleSortParams(sort));
        Specification<Team> spec = TeamSpecification.hasIsActive(isActive).and(TeamSpecification.hasName(name)).and(TeamSpecification.hasState(state));
        List<Team> teams = repository.findAll(spec, pageable).getContent();
        return Utils.convertToTeamDTO(teams);
    }

    public String delete(Long id) {
        Team team = getTeamById(id, false);
        team.setIsActive(false);
        repository.save(team);
        return "";
    }

    public RetrospectDTO getRetrospect(Long id, String matchLocation, boolean isHammering) {
        Team team = getTeamById(id, false);
        return Utils.createRetrospectDTO(team, null, matchLocation, isHammering);
    }

    public RetrospectDTO getRetrospect(Long id, Long opponentId, String matchLocation, boolean isHammering) {
        Team team = getTeamById(id, false);
        Team opponentTeam = getTeamById(opponentId, true);

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
        List<Team> teams = getTeamsByMatchLocation(rankBy, matchLocation);
        return Utils.rankTeams(Utils.convertToTeamDTO(teams), comparator, filter);
    }

    public Team getTeamById(Long id, boolean isOpponent) {
        Team team = repository.findById(id);
        if (team == null) {
            throw new NotFoundException(isOpponent ? "Opponent team not found" : "Team not found");
        }
        return team;
    }

    public List<Team> getTeamsByMatchLocation(String rankBy, String matchLocation) {
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
}

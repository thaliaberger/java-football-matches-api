package com.meli.football_matches_api.service;

import com.meli.football_matches_api.dto.RetrospectDTO;
import com.meli.football_matches_api.dto.TeamDTO;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.ranking.RankingGenerator;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.specification.TeamSpecification;
import com.meli.football_matches_api.utils.Utils;
import com.meli.football_matches_api.validations.TeamValidations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeamService {

    private static final String TEAM_NOT_FOUND_MESSAGE = "Team not found";

    private final TeamRepository repository;

    private final RankingGenerator rankingGenerator;

    public TeamService(TeamRepository repository, RankingGenerator rankingGenerator) {
        this.repository = repository;
        this.rankingGenerator = rankingGenerator;
    }

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
        Team team = repository.findById(id).orElseThrow(() -> new NotFoundException(TEAM_NOT_FOUND_MESSAGE));
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
        getTeamById(opponentId, true);

        return Utils.createRetrospectDTO(team, opponentId, matchLocation, isHammering);
    }

    public Map<String, RetrospectDTO> getRetrospectAgainstAll(Long id) {
        Team team = repository.findById(id).orElseThrow(() -> new NotFoundException(TEAM_NOT_FOUND_MESSAGE));

        List<Match> homeMatches = team.getHomeMatches();
        List<Match> awayMatches = team.getAwayMatches();

        HashMap<String, RetrospectDTO> retrospectsByOpponent = new HashMap<>();

        Utils.populateRetrospectsByOpponentHashMap(homeMatches, true, retrospectsByOpponent);
        Utils.populateRetrospectsByOpponentHashMap(awayMatches, false, retrospectsByOpponent);

        return retrospectsByOpponent;
    }

    public List<TeamDTO> ranking(String rankBy, String matchLocation) {
        if (matchLocation == null) return rankingGenerator.createGenerator(rankBy).execute();
        return rankingGenerator.createGenerator(rankBy, matchLocation).execute();
    }

    public Team getTeamById(Long id, boolean isOpponent) {
        String message = isOpponent ? "Opponent team not found" : TEAM_NOT_FOUND_MESSAGE;
        return repository.findById(id).orElseThrow(() -> new NotFoundException(message));
    }
}

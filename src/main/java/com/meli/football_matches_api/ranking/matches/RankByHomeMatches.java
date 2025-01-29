package com.meli.football_matches_api.ranking.matches;

import com.meli.football_matches_api.dto.TeamDTO;
import com.meli.football_matches_api.ranking.Ranking;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.utils.TeamFilter;
import com.meli.football_matches_api.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class RankByHomeMatches implements Ranking {

    private final TeamRepository teamRepository;

    public RankByHomeMatches(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public List<TeamDTO> execute() {
        return rankTeams(Utils.convertToTeamDTO(teamRepository.findByHomeMatchesNotNull()));
    }

    @Override
    public String getRankBy() {
        return "matches";
    }

    @Override
    public String getMatchLocation() {
        return "home";
    }

    @Override
    public Comparator<TeamDTO> getComparator() {
        return Comparator.comparing(TeamDTO::getNumberOfHomeMatches).reversed();
    }

    @Override
    public TeamFilter getFilter() {
        return null;
    }
}

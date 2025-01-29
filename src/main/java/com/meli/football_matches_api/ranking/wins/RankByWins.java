package com.meli.football_matches_api.ranking.wins;

import com.meli.football_matches_api.dto.TeamDTO;
import com.meli.football_matches_api.ranking.Ranking;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.utils.TeamFilter;
import com.meli.football_matches_api.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class RankByWins implements Ranking {

    private final TeamRepository teamRepository;

    public RankByWins(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public List<TeamDTO> execute() {
        return rankTeams(Utils.convertToTeamDTO(teamRepository.findByHomeMatchesNotNullOrAwayMatchesNotNull()));
    }

    @Override
    public String getRankBy() {
        return "wins";
    }

    @Override
    public String getMatchLocation() { return ""; }

    @Override
    public Comparator<TeamDTO> getComparator() {
        return Comparator.comparing(TeamDTO::getWins).reversed();
    }

    @Override
    public TeamFilter getFilter() {
        return team -> team.getWins() != 0;
    }
}

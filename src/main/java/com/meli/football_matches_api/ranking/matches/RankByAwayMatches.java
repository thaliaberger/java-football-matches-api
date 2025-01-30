package com.meli.football_matches_api.ranking.matches;

import com.meli.football_matches_api.dto.TeamDTO;
import com.meli.football_matches_api.enums.MatchLocation;
import com.meli.football_matches_api.enums.RankBy;
import com.meli.football_matches_api.ranking.Ranking;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.utils.TeamFilter;
import com.meli.football_matches_api.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class RankByAwayMatches implements Ranking {

    private final TeamRepository teamRepository;

    public RankByAwayMatches(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public List<TeamDTO> execute() {
        return rankTeams(Utils.convertToTeamDTO(teamRepository.findByAwayMatchesNotNull()));
    }

    @Override
    public RankBy getRankBy() {
        return RankBy.MATCHES;
    }

    @Override
    public MatchLocation getMatchLocation() {
        return MatchLocation.AWAY;
    }

    @Override
    public Comparator<TeamDTO> getComparator() {
        return Comparator.comparing(TeamDTO::getNumberOfAwayMatches).reversed();
    }

    @Override
    public TeamFilter getFilter() {
        return null;
    }
}

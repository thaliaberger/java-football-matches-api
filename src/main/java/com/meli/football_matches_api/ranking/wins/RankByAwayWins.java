package com.meli.football_matches_api.ranking.wins;

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
public class RankByAwayWins implements Ranking {

    private final TeamRepository teamRepository;

    public RankByAwayWins(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public List<TeamDTO> execute() {
        return rankTeams(Utils.convertToTeamDTO(teamRepository.findByAwayMatchesAwayGoalsNotNull()));
    }

    @Override
    public RankBy getRankBy() {
        return RankBy.WINS;
    }

    @Override
    public MatchLocation getMatchLocation() {
        return MatchLocation.AWAY;
    }

    @Override
    public Comparator<TeamDTO> getComparator() {
        return Comparator.comparing(TeamDTO::getAwayWins).reversed();
    }

    @Override
    public TeamFilter getFilter() {
        return team -> team.getWins() != 0;
    }
}

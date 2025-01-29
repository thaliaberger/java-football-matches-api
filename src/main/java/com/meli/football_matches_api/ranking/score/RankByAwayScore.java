package com.meli.football_matches_api.ranking.score;

import com.meli.football_matches_api.dto.TeamDTO;
import com.meli.football_matches_api.ranking.Ranking;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.utils.TeamFilter;
import com.meli.football_matches_api.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class RankByAwayScore implements Ranking {

    private final TeamRepository teamRepository;

    public RankByAwayScore(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public List<TeamDTO> execute() {
        return rankTeams(Utils.convertToTeamDTO(teamRepository.findByAwayMatchesNotNull()));
    }

    @Override
    public String getRankBy() {
        return "score";
    }

    @Override
    public String getMatchLocation() {
        return "away";
    }

    @Override
    public Comparator<TeamDTO> getComparator() {
        return Comparator.comparing(TeamDTO::getScoreFromAwayMatches).reversed();
    }

    @Override
    public TeamFilter getFilter() {
        return team -> team.getScore() != 0;
    }
}

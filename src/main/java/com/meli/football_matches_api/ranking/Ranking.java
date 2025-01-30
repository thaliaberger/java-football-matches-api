package com.meli.football_matches_api.ranking;

import com.meli.football_matches_api.dto.TeamDTO;
import com.meli.football_matches_api.enums.MatchLocation;
import com.meli.football_matches_api.enums.RankBy;
import com.meli.football_matches_api.utils.TeamFilter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public interface Ranking {
    List<TeamDTO> execute();
    RankBy getRankBy();
    MatchLocation getMatchLocation();
    Comparator<TeamDTO> getComparator();
    TeamFilter getFilter();

    default List<TeamDTO> rankTeams(List<TeamDTO> teams) {
        List<TeamDTO> filteredTeams = new ArrayList<>();
        TeamFilter teamFilter = getFilter();

        for (TeamDTO team : teams) {
            if (teamFilter == null || teamFilter.filter(team)) filteredTeams.add(team);
        }

        filteredTeams.sort(getComparator());

        return filteredTeams;
    }

}

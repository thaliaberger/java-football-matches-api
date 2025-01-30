package com.meli.football_matches_api.ranking;

import com.meli.football_matches_api.enums.MatchLocation;
import com.meli.football_matches_api.enums.RankBy;
import com.meli.football_matches_api.ranking.score.RankByScore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RankingGenerator {

    private final List<Ranking> rankings;

    private final RankByScore defaultRanking;

    public RankingGenerator(List<Ranking> rankings, RankByScore defaultRanking) {
        this.rankings = rankings;
        this.defaultRanking = defaultRanking;
    }

    public Ranking createGenerator(String rankBy, String matchLocation) {
        RankBy rankByEnum = RankBy.convertStringToEnum(rankBy);
        MatchLocation matchLocationEnum = MatchLocation.convertStringToEnum(matchLocation);

        for (Ranking ranking : this.rankings) {
            if (rankByEnum.equals(ranking.getRankBy()) && matchLocationEnum.equals(ranking.getMatchLocation())) return ranking;
        }

        return defaultRanking;
    }

    public Ranking createGenerator(String rankBy) {
        return createGenerator(rankBy, "ALL");
    }
}

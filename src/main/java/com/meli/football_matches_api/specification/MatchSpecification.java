package com.meli.football_matches_api.specification;

import com.meli.football_matches_api.model.Match;
import org.springframework.data.jpa.domain.Specification;

public class MatchSpecification {
    public static Specification<Match> hasHomeTeam(Long teamId) {
        return (root, query, criteriaBuilder) -> {
            if (teamId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("homeTeam").get("id"), teamId);
        };
    }

    public static Specification<Match> hasAwayTeam(Long teamId) {
        return (root, query, criteriaBuilder) -> {
            if (teamId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("awayTeam").get("id"), teamId);
        };
    }

    public static Specification<Match> hasStadium(Long stadiumId) {
        return (root, query, criteriaBuilder) -> {
            if (stadiumId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("stadium"), stadiumId);
        };
    }
}

package com.meli.football_matches_api.specification;

import com.meli.football_matches_api.model.Team;
import org.springframework.data.jpa.domain.Specification;

public class TeamSpecification {
    public static Specification<Team> hasIsActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isActive"), isActive);
        };
    }

    public static Specification<Team> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("name"), "%" + name + "%");
        };
    }

    public static Specification<Team> hasState(String state) {
        return (root, query, criteriaBuilder) -> {
            if (state == null || state.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("state"), state);
        };
    }
}


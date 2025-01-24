package com.meli.football_matches_api.specification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.meli.football_matches_api.model.Match;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

public class MatchSpecificationTest {

    @Mock
    private Root<Match> root;
    @Mock
    private CriteriaQuery<Match> query;
    @Mock
    private CriteriaBuilder criteriaBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should not filter by homeTeam")
    void hasHomeTeamCaseNullId() {
        Specification<Match> spec = MatchSpecification.hasHomeTeam(null);
        assertEquals(criteriaBuilder.conjunction(), spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    @DisplayName("Should not filter by awayTeam")
    void hasAwayTeamCaseNullId() {
        Specification<Match> spec = MatchSpecification.hasAwayTeam(null);
        assertEquals(criteriaBuilder.conjunction(), spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    @DisplayName("Should filter by Stadium")
    void hasStadiumCaseSuccess() {
        Long stadiumId = 2L;
        when(root.get("stadium")).thenReturn(mock(Path.class));

        Specification<Match> spec = MatchSpecification.hasStadium(stadiumId);
        assertEquals(criteriaBuilder.equal(root.get("stadium"), stadiumId), spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    @DisplayName("Should not filter by Stadium")
    void hasStadiumCaseNullId() {
        Specification<Match> spec = MatchSpecification.hasStadium(null);
        assertEquals(criteriaBuilder.conjunction(), spec.toPredicate(root, query, criteriaBuilder));
    }
}

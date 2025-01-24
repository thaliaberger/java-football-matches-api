package com.meli.football_matches_api.specification;

import com.meli.football_matches_api.controller.MatchController;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.repository.MatchRepository;
import com.meli.football_matches_api.repository.StadiumRepository;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.service.MatchService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchSpecificationTest {

    private MockMvc mockMvc;

    @Mock
    private Root<Match> root;

    @Mock
    private CriteriaQuery<Match> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private StadiumRepository stadiumRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private MatchService matchService;

    @InjectMocks
    private MatchController matchController;


    @Test
    @DisplayName("Should not filter by homeTeam")
    void hasHomeTeamCaseNullId() {
        Long teamId = null;
        Specification<Match> spec = MatchSpecification.hasHomeTeam(teamId);
        assertEquals(criteriaBuilder.conjunction(), spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    @DisplayName("Should not filter by awayTeam")
    void hasAwayTeamCaseNullId() {
        Long teamId = null;
        Specification<Match> spec = MatchSpecification.hasAwayTeam(teamId);
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
        Long stadiumId = null;
        Specification<Match> spec = MatchSpecification.hasStadium(stadiumId);
        assertEquals(criteriaBuilder.conjunction(), spec.toPredicate(root, query, criteriaBuilder));
    }
}

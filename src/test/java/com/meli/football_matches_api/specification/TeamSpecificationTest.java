package com.meli.football_matches_api.specification;

import com.meli.football_matches_api.model.Team;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TeamSpecificationTest {

    @Mock
    private Root<Team> root;
    @Mock
    private CriteriaQuery<Team> query;
    @Mock
    private CriteriaBuilder criteriaBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should filter by isActive")
    void hasIsActiveCaseSuccess() {
        when(root.get("isActive")).thenReturn(mock(Path.class));

        Specification<Team> spec = TeamSpecification.hasIsActive(true);
        assertEquals(criteriaBuilder.equal(root.get("isActive"), true), spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    @DisplayName("Should filter by name")
    void hasNameCaseSuccess() {
        when(root.get("name")).thenReturn(mock(Path.class));

        Specification<Team> spec = TeamSpecification.hasName("Flamengo");
        assertEquals(criteriaBuilder.equal(root.get("name"), true), spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    @DisplayName("Should filter by state")
    void hasStateCaseSuccess() {
        when(root.get("state")).thenReturn(mock(Path.class));

        Specification<Team> spec = TeamSpecification.hasState("RJ");
        assertEquals(criteriaBuilder.equal(root.get("state"), true), spec.toPredicate(root, query, criteriaBuilder));
    }
}

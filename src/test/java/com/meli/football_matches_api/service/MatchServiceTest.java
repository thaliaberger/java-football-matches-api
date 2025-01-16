package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.DTO.StadiumDTO;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.MatchRepository;
import com.meli.football_matches_api.repository.StadiumRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchService matchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should get Match successfully")
    void getCaseSuccess() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        Long matchId = 1L;
        Match match = new Match(matchId, 1, 0, LocalDateTime.of(2000, 1, 2, 10, 10, 10), team1, team2, stadium);

        when(matchRepository.findById(matchId)).thenReturn(match);

        ResponseEntity<MatchDTO> response = matchService.get(matchId);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(matchId, response.getBody().getId());
    }

    @Test
    @DisplayName("Should get all matches successfully")
    void listCaseSuccess() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        Long matchId = 1L;
        List<Match> matches = new ArrayList<>();
        Match match = new Match(matchId, 1, 0, LocalDateTime.of(2000, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2000, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match);
        matches.add(match2);

        when(matchRepository.findAll()).thenReturn(matches);

        ResponseEntity<List<MatchDTO>> response = matchService.list();

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(matchId, response.getBody().getFirst().getId());
    }
}
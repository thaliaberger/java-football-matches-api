package com.meli.football_matches_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.meli.football_matches_api.dto.MatchDTO;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.MatchRepository;
import com.meli.football_matches_api.repository.StadiumRepository;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
class MatchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private StadiumRepository stadiumRepository;

    @Mock
    private MatchService matchService;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private MatchController matchController;

    Team team1 = new Team();
    Team team2 = new Team();
    Stadium stadium = new Stadium();
    List<Match> matches = new ArrayList<>();
    Match match1 = new Match();
    Match match2 = new Match();
    Match newMatch = new Match();
    MatchDTO matchDTO = new MatchDTO();
    long matchId = 3;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(matchController).build();

        team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1970, 1, 1), true);
        team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        stadium = new Stadium(1L, "Morumbi", null, null);
        match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        newMatch = new Match(matchId, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        matchDTO = new MatchDTO(newMatch);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("Should create Match successfully")
    void createCaseSuccess() throws Exception {
        when(matchService.create(any(MatchDTO.class))).thenReturn(matchDTO);

        mockMvc.perform(post("/match")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(matchDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(matchId));
    }

    @Test
    @DisplayName("Should update Match successfully")
    void updateCaseSuccess() throws Exception {
        when(matchService.update(any(MatchDTO.class))).thenReturn(matchDTO);

        mockMvc.perform(put("/match")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(matchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(matchId));
    }

    @Test
    @DisplayName("Should delete Match successfully")
    void deleteCaseSuccess() throws Exception {
        when(matchService.delete(matchId)).thenReturn("");

        mockMvc.perform(delete("/match?id=" + matchId)
                        .contentType("application/json")
                        .content(""))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should get Match successfully")
    void getCaseSuccess() throws Exception {
        when(matchService.get(matchId)).thenReturn(matchDTO);

        mockMvc.perform(get("/match?id=" + matchId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(matchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(matchId));
    }
}

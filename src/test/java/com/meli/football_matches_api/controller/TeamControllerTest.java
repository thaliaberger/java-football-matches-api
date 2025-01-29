package com.meli.football_matches_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.meli.football_matches_api.dto.RetrospectDTO;
import com.meli.football_matches_api.dto.TeamDTO;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.service.TeamService;
import com.meli.football_matches_api.utils.Utils;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TeamControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    long teamId = 1L;
    Team team1 = new Team();
    Team team2 = new Team();
    Team team3 = new Team();
    TeamDTO teamDTO = new TeamDTO();
    List<Match> team1HomeMatches = new ArrayList<>();
    List<Match> team1AwayMatches = new ArrayList<>();
    List<Team> teams = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();

        team1 = new Team(teamId, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        team3 = new Team(3L, "Figueirense", "SC", LocalDate.of(1980, 1, 1), true);
        teamDTO = new TeamDTO(team1);

        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        Match match = new Match(1L, 4, 0, LocalDateTime.of(2000, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2000, 1, 3, 10, 10, 10), team1, team2, stadium);
        Match match3 = new Match(3L, 2, 1, LocalDateTime.of(2000, 1, 3, 10, 10, 10), team3, team1, stadium);

        team1HomeMatches.add(match);
        team1HomeMatches.add(match2);
        team1.setHomeMatches(team1HomeMatches);
        team1AwayMatches.add(match3);
        team1.setAwayMatches(team1AwayMatches);
        team2.setAwayMatches(team1HomeMatches);
        team3.setHomeMatches(team1AwayMatches);

        teams.add(team3);
        teams.add(team2);
        teams.add(team1);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("Should create Team successfully")
    void createCaseSuccess() throws Exception {
        when(teamService.create(any(TeamDTO.class))).thenReturn(teamDTO);

        mockMvc.perform(post("/team")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(teamDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(teamId));
    }

    @Test
    @DisplayName("Should update Team successfully")
    void updateCaseSuccess() throws Exception {
        when(teamService.update(any(TeamDTO.class))).thenReturn(teamDTO);

        mockMvc.perform(put("/team")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(teamDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teamId));
    }

    @Test
    @DisplayName("Should delete Team successfully")
    void deleteCaseSuccess() throws Exception {
        when(teamService.delete(teamId)).thenReturn("");

        mockMvc.perform(delete("/team?id=" + teamId)
                        .contentType("application/json")
                        .content(""))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should get Team successfully")
    void getCaseSuccess() throws Exception {
        when(teamService.get(teamId)).thenReturn(teamDTO);

        mockMvc.perform(get("/team?id=" + teamId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(teamDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teamId));
    }

    @Test
    @DisplayName("Should get list of Teams successfully")
    void listCaseSuccess() throws Exception {
        when(teamService.list(0, 1000, "id,asc", null, null, null)).thenReturn(Utils.convertToTeamDTO(teams));

        mockMvc.perform(get("/team/list")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(teams)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3));
    }

    @Test
    @DisplayName("Should get ranked list of Teams successfully")
    void rankingCaseSuccess() throws Exception {
        List<Team> rankedTeams = new ArrayList<>();
        rankedTeams.add(team1);
        rankedTeams.add(team3);
        when(teamService.ranking("goals", null)).thenReturn(Utils.convertToTeamDTO(rankedTeams));

        mockMvc.perform(get("/team/ranking?rankBy=goals")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(teams)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    @DisplayName("Should get Team retrospect successfully")
    void retrospectCaseSuccess() throws Exception {
        RetrospectDTO retrospectDTO = new RetrospectDTO(team1HomeMatches, team1AwayMatches);
        when(teamService.getRetrospect(teamId, "", false)).thenReturn(retrospectDTO);

        mockMvc.perform(get("/team/retrospect?id=" + teamId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(teams)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wins").value(1));
    }
}

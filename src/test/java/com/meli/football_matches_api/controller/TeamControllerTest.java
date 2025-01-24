package com.meli.football_matches_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

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
    TeamDTO teamDTO = new TeamDTO();
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();

        team1 = new Team(teamId, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        teamDTO = new TeamDTO(team1);

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
}

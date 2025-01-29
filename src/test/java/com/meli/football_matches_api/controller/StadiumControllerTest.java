package com.meli.football_matches_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.meli.football_matches_api.dto.StadiumDTO;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.service.StadiumService;
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

class StadiumControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private StadiumService stadiumService;

    @InjectMocks
    private StadiumController stadiumController;

    Team team;
    StadiumDTO stadiumDTO;
    long stadiumId = 1;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(stadiumController).build();
        team = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        stadiumDTO = new StadiumDTO(stadiumId, "Maracanã", team, null);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("Should create Match successfully")
    void createCaseSuccess() throws Exception {
        when(stadiumService.create(any(StadiumDTO.class))).thenReturn(stadiumDTO);

        mockMvc.perform(post("/stadium")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(stadiumDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(stadiumId));
    }

    @Test
    @DisplayName("Should update Match successfully")
    void updateCaseSuccess() throws Exception {
        when(stadiumService.update(any(StadiumDTO.class))).thenReturn(stadiumDTO);

        mockMvc.perform(put("/stadium")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(stadiumDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stadiumId));
    }

    @Test
    @DisplayName("Should get Match successfully")
    void getCaseSuccess() throws Exception {
        when(stadiumService.get(stadiumId)).thenReturn(stadiumDTO);

        mockMvc.perform(get("/stadium?id=" + stadiumId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(stadiumDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stadiumId));
    }
}

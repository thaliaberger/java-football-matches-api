package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TeamServiceTest {

    @Mock
    private TeamRepository repository;

    @InjectMocks
    private TeamService teamService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCaseSuccess() {
        TeamDTO teamDTO = new TeamDTO(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);

        when(repository.save(any(Team.class))).thenReturn(new Team(teamDTO));
        ResponseEntity<TeamDTO> response = teamService.create(teamDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(teamDTO.getName(), response.getBody().getName());
    }

    @Test
    void updateCaseSuccess() {
        TeamDTO teamDTO = new TeamDTO(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(any(Team.class))).thenReturn(new Team(teamDTO));

        ResponseEntity<TeamDTO> response = teamService.update(teamDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(teamDTO.getName(), response.getBody().getName());
    }

    @Test
    void updateCaseTeamDoesNotExist() {
        TeamDTO teamDTO = new TeamDTO(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);

        when(repository.findById(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> teamService.update(teamDTO));
    }
}
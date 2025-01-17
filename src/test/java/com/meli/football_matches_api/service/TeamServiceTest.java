package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Should create Team successfully")
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
    @DisplayName("Should throw FieldException when Team name is empty or null")
    void createCaseEmptyName() {
        TeamDTO teamDTO = new TeamDTO(1L, "", "RJ", LocalDate.of(1980, 1, 1), true);

        FieldException exception = assertThrows(FieldException.class, () -> {
            teamService.create(teamDTO);
        });

        assertEquals("[name] cannot be empty or null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when Team isActive is null")
    void createCaseIsActiveNull() {
        TeamDTO teamDTO = new TeamDTO(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            teamService.create(teamDTO);
        });

        assertEquals("[isActive] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException when Team already exists")
    void createCaseTeamAlreadyExists() {
        TeamDTO teamDTO = new TeamDTO(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        TeamDTO newTeamDTO = new TeamDTO(2L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);

        when(repository.findByNameAndStateAndIdNot(newTeamDTO.getName(), newTeamDTO.getState(), newTeamDTO.getId())).thenReturn(new Team(teamDTO));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            teamService.create(newTeamDTO);
        });

        assertEquals("Already existing team with name [Flamengo] and state [RJ]", exception.getMessage());
    }

    @Test
    @DisplayName("Should update Team successfully")
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
    @DisplayName("Should throw NotFoundException when updating Team that does not exist")
    void updateCaseTeamDoesNotExist() {
        TeamDTO teamDTO = new TeamDTO(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);

        when(repository.findById(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> teamService.update(teamDTO));
    }
}
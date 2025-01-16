package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.StadiumDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.repository.StadiumRepository;
import com.meli.football_matches_api.validations.StadiumValidations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StadiumServiceTest {

    @Mock
    private StadiumRepository stadiumRepository;

    @InjectMocks
    private StadiumService stadiumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create Stadium successfully")
    void createCaseSuccess() {
        StadiumDTO stadiumDTO = new StadiumDTO(1L, "Maracanã", null, null);

        when(stadiumRepository.save(any(Stadium.class))).thenReturn(new Stadium(stadiumDTO));

        ResponseEntity<StadiumDTO> response = stadiumService.create(stadiumDTO);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Maracanã", response.getBody().getName());
    }

    @Test
    @DisplayName("Should throw FieldException when stadium name is empty")
    void createCaseEmptyStadiumName() {

        FieldException exception = assertThrows(FieldException.class, () -> {
            StadiumDTO stadiumDTO = new StadiumDTO(1L, null, null, null);
            StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository, false);
        });

        assertEquals("Stadium name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when stadium name has less then 3 characters")
    void createCaseStadiumNameSmallerThen3Characters() {

        FieldException exception = assertThrows(FieldException.class, () -> {
            Team team = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
            StadiumDTO stadiumDTO = new StadiumDTO(1L, "Ma", team, null);
            StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository, false);
        });

        assertEquals("Stadium name must be at least 3 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException when already exists Stadium with same name")
    void createCaseAlreadyExistingStadiumWithSameName() {
        StadiumDTO stadiumDTO = new StadiumDTO(1L, "Maracanã", null, null);
        Stadium newStadium = new Stadium(stadiumDTO);

        when(stadiumRepository.existsByName("Maracanã")).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            StadiumValidations.validateName("Maracanã", stadiumRepository, false);
        });

        assertEquals("Stadium with name [Maracanã] already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Should update Stadium successfully")
    void updateCaseSuccess() {
        StadiumDTO stadiumDTO = new StadiumDTO(1L, "Morumbi", null, null);

        when(stadiumRepository.existsById(1L)).thenReturn(true);
        when(stadiumRepository.save(any(Stadium.class))).thenReturn(new Stadium(stadiumDTO));

        ResponseEntity<StadiumDTO> response = stadiumService.update(stadiumDTO);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Morumbi", response.getBody().getName());
    }

    @Test
    @DisplayName("Should throw FieldException when stadium name is empty")
    void updateCaseEmptyStadiumName() {

        FieldException exception = assertThrows(FieldException.class, () -> {
            StadiumDTO stadiumDTO = new StadiumDTO(1L, null, null, null);
            StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository, true);
        });

        assertEquals("Stadium name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when stadium name has less then 3 characters")
    void updateCaseStadiumNameSmallerThen3Characters() {

        FieldException exception = assertThrows(FieldException.class, () -> {
            StadiumDTO stadiumDTO = new StadiumDTO(1L, "Ma", null, null);
            StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository, true);
        });

        assertEquals("Stadium name must be at least 3 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when trying to update a Stadium without passing an id")
    void updateCaseWithoutPassingAnId() {

        FieldException exception = assertThrows(FieldException.class, () -> {
            StadiumDTO stadiumDTO = new StadiumDTO(null, "Maracanã", null, null);
            StadiumValidations.validateIfStadiumExists(stadiumDTO, stadiumRepository);
        });

        assertEquals("You need to provide a valid id to update a stadium", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NotFoundException when trying to update an non-existing Stadium")
    void updateCaseStadiumDoesNotExists() {

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            StadiumDTO stadiumDTO = new StadiumDTO(1L, "Maracanã", null, null);
            StadiumValidations.validateIfStadiumExists(stadiumDTO, stadiumRepository);
        });

        assertEquals("Stadium not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get Stadium successfully")
    void getCaseSuccess() {
        Long stadiumId = 1L;
        Stadium stadium = new Stadium(stadiumId, "Maracanã", null, null);
        when(stadiumRepository.findById(stadiumId)).thenReturn(stadium);

        ResponseEntity<StadiumDTO> response = stadiumService.get(stadiumId);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(stadiumId, response.getBody().getId());
        assertEquals("Maracanã", response.getBody().getName());
    }

    @Test
    @DisplayName("Should throw NotFoundException when stadium does not exist")
    void getStadiumCaseStadiumDoesNotExist() {
        Long stadiumId = 1L;
        when(stadiumRepository.findById(stadiumId)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            stadiumService.get(stadiumId);
        });

        assertEquals("Stadium not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get all Stadiums successfully")
    void listCaseSuccess() {
        List<Stadium> stadiumList = new ArrayList<>();
        Stadium stadium = new Stadium(1L, "Maracanã", null, null);
        Stadium stadium2 = new Stadium(2L, "Morumbi", null, null);
        stadiumList.add(stadium);
        stadiumList.add(stadium2);

        when(stadiumRepository.findAll()).thenReturn(stadiumList);

        ResponseEntity<List<StadiumDTO>> response = stadiumService.list();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Maracanã", response.getBody().get(0).getName());
        assertEquals("Morumbi", response.getBody().get(1).getName());
    }
}
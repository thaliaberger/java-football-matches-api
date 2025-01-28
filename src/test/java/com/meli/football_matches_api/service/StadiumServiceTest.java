package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.StadiumDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StadiumServiceTest {

    @Mock
    private StadiumRepository stadiumRepository;

    @InjectMocks
    private StadiumService stadiumService;

    Team team;
    StadiumDTO stadiumDTO;
    long stadiumId = 1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        team = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        stadiumDTO = new StadiumDTO(stadiumId, "Maracanã", team, null);
    }

    @Test
    @DisplayName("Should create Stadium successfully")
    void createCaseSuccess() {
        when(stadiumRepository.save(any(Stadium.class))).thenReturn(new Stadium(stadiumDTO));

        StadiumDTO response = stadiumService.create(stadiumDTO);

        assertNotNull(response);
        assertEquals("Maracanã", response.getName());
    }

    @Test
    @DisplayName("Should throw FieldException when stadium name is null or empty")
    void createCaseEmptyStadiumName() {
        stadiumDTO.setName(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            stadiumService.create(stadiumDTO);
        });

        assertEquals("[name] cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when stadium name has less then 3 characters")
    void createCaseStadiumNameSmallerThen3Characters() {
        stadiumDTO.setName("Ma");

        FieldException exception = assertThrows(FieldException.class, () -> {
            stadiumService.create(stadiumDTO);
        });

        assertEquals("[name] must be at least 3 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException when already exists Stadium with same name")
    void createCaseAlreadyExistingStadiumWithSameName() {

        when(stadiumRepository.existsByName("Maracanã")).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            stadiumService.create(stadiumDTO);
        });

        assertEquals("Stadium with name [Maracanã] already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Should update Stadium successfully")
    void updateCaseSuccess() {
        StadiumDTO newStadiumDTO = new StadiumDTO(1L, "Morumbi", null, null);

        when(stadiumRepository.existsById(stadiumId)).thenReturn(true);
        when(stadiumRepository.save(any(Stadium.class))).thenReturn(new Stadium(newStadiumDTO));

        StadiumDTO response = stadiumService.update(newStadiumDTO);

        assertNotNull(response);
        assertEquals("Morumbi", response.getName());
    }

    @Test
    @DisplayName("Should throw FieldException when stadium name is empty")
    void updateCaseEmptyStadiumName() {
        stadiumDTO.setName("");

        when(stadiumRepository.existsById(stadiumId)).thenReturn(true);

        FieldException exception = assertThrows(FieldException.class, () -> {
            stadiumService.update(stadiumDTO);
        });

        assertEquals("[name] cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when stadium name has less then 3 characters")
    void updateCaseStadiumNameSmallerThen3Characters() {
        stadiumDTO.setName("Ma");

        when(stadiumRepository.existsById(stadiumId)).thenReturn(true);

        FieldException exception = assertThrows(FieldException.class, () -> {
            stadiumService.update(stadiumDTO);
        });

        assertEquals("[name] must be at least 3 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when trying to update a Stadium without passing an id")
    void updateCaseWithoutPassingAnId() {
        stadiumDTO.setId(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            stadiumService.update(stadiumDTO);
        });

        assertEquals("You need to provide a valid id to update a stadium", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NotFoundException when trying to update an non-existing Stadium")
    void updateCaseStadiumDoesNotExists() {
        when(stadiumRepository.existsById(stadiumId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            stadiumService.update(stadiumDTO);
        });

        assertEquals("Stadium not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get Stadium successfully")
    void getCaseSuccess() {
        Stadium stadium = new Stadium(stadiumDTO);
        when(stadiumRepository.findById(stadiumId)).thenReturn(Optional.of(stadium));

        StadiumDTO response = stadiumService.get(stadiumId);

        assertNotNull(response);
        assertEquals(stadiumId, response.getId());
        assertEquals("Maracanã", response.getName());
    }

    @Test
    @DisplayName("Should throw NotFoundException when stadium does not exist")
    void getStadiumCaseStadiumDoesNotExist() {
        when(stadiumRepository.findById(3L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            stadiumService.get(3L);
        });

        assertEquals("Stadium not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get all Stadiums successfully")
    void listCaseSuccess() {
        List<Stadium> stadiumList = new ArrayList<>();
        Stadium stadium = new Stadium(stadiumDTO);
        Stadium stadium2 = new Stadium(2L, "Morumbi", null, null);
        stadiumList.add(stadium);
        stadiumList.add(stadium2);

        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "id"));
        when(stadiumRepository.findAll(pageable)).thenReturn(new PageImpl<Stadium>(stadiumList));

        List<StadiumDTO> response = stadiumService.list(0, 1000, "id,asc");

        assertNotNull(response);
        assertEquals("Maracanã", response.get(0).getName());
        assertEquals("Morumbi", response.get(1).getName());
    }
}
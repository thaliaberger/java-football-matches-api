package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.StadiumDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.repository.StadiumRepository;
import com.meli.football_matches_api.validations.StadiumValidations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StadiumServiceTest {

    @Mock
    private StadiumRepository stadiumRepository;

    @InjectMocks
    private StadiumService stadiumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should create Stadium successfully")
    void createCaseSuccess() {
        StadiumDTO stadiumDTO = new StadiumDTO(1L, "Maracanã", null, null);
        StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository, false);
        Stadium newStadium = new Stadium(stadiumDTO);
        stadiumRepository.save(newStadium);

        verify(stadiumRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw FieldException when stadium name is empty")
    void createCaseEmptyStadiumName() {

        FieldException thrown = Assertions.assertThrows(FieldException.class, () -> {
            StadiumDTO stadiumDTO = new StadiumDTO(1L, null, null, null);
            StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository, false);
        });

        Assertions.assertEquals("Stadium name cannot be null or empty", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when stadium name has less then 3 characters")
    void createCaseStadiumNameSmallerThen3Characters() {

        FieldException thrown = Assertions.assertThrows(FieldException.class, () -> {
            Team team = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
            StadiumDTO stadiumDTO = new StadiumDTO(1L, "Ma", team, null);
            StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository, false);
        });

        Assertions.assertEquals("Stadium name must be at least 3 characters", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException when already exists Stadium with same name")
    void createCaseAlreadyExistingStadiumWithSameName() {
        StadiumDTO stadiumDTO = new StadiumDTO(1L, "Maracanã", null, null);
        Stadium newStadium = new Stadium(stadiumDTO);

        when(stadiumRepository.existsByName("Maracanã")).thenReturn(true);

        ConflictException thrown = Assertions.assertThrows(ConflictException.class, () -> {
            StadiumValidations.validateName("Maracanã", stadiumRepository, false);
        });

        Assertions.assertEquals("Stadium with name [Maracanã] already exists", thrown.getMessage());
    }

    @Test
    @DisplayName("Should update Stadium successfully")
    void updateCaseSuccess() {
        StadiumDTO stadiumDTO = new StadiumDTO(1L, "Maracanã", null, null);
        StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository, true);
        Stadium newStadium = new Stadium(stadiumDTO);
        stadiumRepository.save(newStadium);

        verify(stadiumRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw FieldException when stadium name is empty")
    void updateCaseEmptyStadiumName() {

        FieldException thrown = Assertions.assertThrows(FieldException.class, () -> {
            StadiumDTO stadiumDTO = new StadiumDTO(1L, null, null, null);
            StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository, true);
        });

        Assertions.assertEquals("Stadium name cannot be null or empty", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when stadium name has less then 3 characters")
    void updateCaseStadiumNameSmallerThen3Characters() {

        FieldException thrown = Assertions.assertThrows(FieldException.class, () -> {
            StadiumDTO stadiumDTO = new StadiumDTO(1L, "Ma", null, null);
            StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository, true);
        });

        Assertions.assertEquals("Stadium name must be at least 3 characters", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when trying to update a Stadium without passing an id")
    void updateCaseWithoutPassingAnId() {

        FieldException thrown = Assertions.assertThrows(FieldException.class, () -> {
            StadiumDTO stadiumDTO = new StadiumDTO(null, "Maracanã", null, null);
            StadiumValidations.validateIfStadiumExists(stadiumDTO, stadiumRepository);
        });

        Assertions.assertEquals("You need to provide a valid id to update a stadium", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw NotFoundException when trying to update an non-existing Stadium")
    void updateCaseStadiumDoesNotExists() {

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            StadiumDTO stadiumDTO = new StadiumDTO(1L, "Maracanã", null, null);
            StadiumValidations.validateIfStadiumExists(stadiumDTO, stadiumRepository);
        });

        Assertions.assertEquals("Stadium not found", thrown.getMessage());
    }
}
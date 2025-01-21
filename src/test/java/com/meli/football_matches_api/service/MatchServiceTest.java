package com.meli.football_matches_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.MatchRepository;
import com.meli.football_matches_api.repository.StadiumRepository;
import com.meli.football_matches_api.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private StadiumRepository stadiumRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private MatchService matchService;

    @Test
    @DisplayName("Should create Match successfully")
    void createCaseSuccess() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        long matchId = 3L;
        Match newMatch = new Match(matchId, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);
        when(stadiumRepository.findById(1L)).thenReturn(stadium);
        when(matchRepository.save(any(Match.class))).thenReturn(new Match(matchDTO));

        MatchDTO response = matchService.create(matchDTO);

        assertNotNull(response);
        assertEquals(matchId, response.getId());
    }

    @Test
    @DisplayName("Should throw FieldException: [homeTeamId] cannot be null")
    void createCaseHomeTeamIdNull() {
        Team team1 = new Team(null, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[homeTeamId] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [awayTeamId] cannot be null")
    void createCaseAwayTeamIdNull() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(null, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[awayTeamId] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [homeTeam] and [awayTeam] cannot be the same")
    void createCaseHomeTeamIdAndAwayTeamIdAreTheSame() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(null, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team1, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[homeTeam] and [awayTeam] cannot be the same", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NotFoundException: homeTeam not found")
    void createCaseHomeTeamNotFound() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("homeTeam not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NotFoundException: awayTeam not found")
    void createCaseAwayTeamNotFound() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("awayTeam not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [homeGoals] cannot be null")
    void createCaseHomeGoalsNull() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, null, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[homeGoals] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [awayGoals] cannot be null")
    void createCaseAwayGoalsNull() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 1, null, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[awayGoals] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [homeGoals] cannot be negative")
    void createCaseHomeGoalsNegative() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, -1, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[homeGoals] cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [awayGoals] cannot be negative")
    void createCaseAwayGoalsNegative() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 1, -1, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[awayGoals] cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [homeTeam] is not active")
    void createCaseHomeTeamNotActive() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), false);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 1, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[homeTeam] is not active", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [awayTeam] is not active")
    void createCaseAwayTeamNotActive() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), false);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 1, 1, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[awayTeam] is not active", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [matchDateTime] cannot be null")
    void createCaseMatchDateTimeIsNull() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 1, 1, null, team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[matchDateTime] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [matchDateTime] cannot be in the future")
    void createCaseMatchDateTimeCannotBeInFuture() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        int nextYear = Year.now().getValue() + 1;
        Match newMatch = new Match(3L, 1, 1, LocalDateTime.of(nextYear, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[matchDateTime] cannot be in the future", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException: [matchDateTime] cannot be before [homeTeamDateCreated]")
    void createCaseMatchDateTimeCannotBeBeforeHomeTeamDateCreated() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 1, 1, LocalDateTime.of(1979, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[matchDateTime] cannot be before [homeTeamDateCreated]", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException: [matchDateTime] cannot be before [awayTeamDateCreated]")
    void createCaseMatchDateTimeCannotBeBeforeAwayTeamDateCreated() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1970, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 1, 1, LocalDateTime.of(1979, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[matchDateTime] cannot be before [awayTeamDateCreated]", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException: Cannot create a match when one of the teams already has a match in less than 48 hours")
    void createCaseTeamAlreadyHasAMatchInLessThan48Hours() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1970, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        Stadium stadium2 = new Stadium(2L, "Maracanã", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 1, 1, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium2);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);
        when(matchRepository.findAllByHomeTeam(any(Team.class))).thenReturn(matches);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("Cannot create a match when one of the teams already has a match in less than 48 hours", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [stadium] cannot be null")
    void createCaseStadiumIsNull() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1970, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 1, 1, LocalDateTime.of(2022, 1, 3, 10, 10, 10), team1, team2, null);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);
        when(matchRepository.findAllByHomeTeam(any(Team.class))).thenReturn(matches);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[stadium] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [stadium.id] cannot be null")
    void createCaseStadiumIdNull() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1970, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        Stadium stadium2 = new Stadium(null, "Maracanã", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 1, 1, LocalDateTime.of(2022, 1, 3, 10, 10, 10), team1, team2, stadium2);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);
        when(matchRepository.findAllByHomeTeam(any(Team.class))).thenReturn(matches);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[stadium.id] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NotFoundException: Stadium not found")
    void createCaseStadiumNotFound() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1970, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        Stadium stadium2 = new Stadium(2L, "Maracanã", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 1, 1, LocalDateTime.of(2022, 1, 3, 10, 10, 10), team1, team2, stadium2);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);
        when(matchRepository.findAllByHomeTeam(any(Team.class))).thenReturn(matches);
        when(stadiumRepository.findById(2L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("Stadium not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException: Stadium already has a match on this date")
    void createCaseStadiumAlreadyHasAMatchOnThisDate() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1970, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team3 = new Team(3L, "Avai", "SC", LocalDate.of(1980, 1, 1), true);
        Team team4 = new Team(4L, "Criciuma", "SC", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match newMatch = new Match(3L, 1, 1, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team3, team4, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        List<Match> matches2 = new ArrayList<>();

        when(teamRepository.findById(3L)).thenReturn(team3);
        when(teamRepository.findById(4L)).thenReturn(team4);
        when(matchRepository.findAllByHomeTeam(any(Team.class))).thenReturn(matches2);
        when(stadiumRepository.findById(1L)).thenReturn(stadium);
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("Stadium already has a match on this date", exception.getMessage());
    }

    @Test
    @DisplayName("Should update Match successfully")
    void updateCaseSuccess() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        List<Match> matches = new ArrayList<>();

        Match match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        Match updatedMatch = new Match(2L, 4, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(updatedMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);
        when(stadiumRepository.findById(1L)).thenReturn(stadium);
        when(matchRepository.save(any(Match.class))).thenReturn(new Match(matchDTO));

        MatchDTO response = matchService.create(matchDTO);

        assertNotNull(response);
        assertEquals(2L, response.getId());
    }

    @Test
    @DisplayName("Should throw NotFoundException: Match not found")
    void updateCaseMatchDoesNotExist() {
        long matchId = 1L;
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        Match updatedMatch = new Match(matchId, 4, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(updatedMatch);

        when(matchRepository.existsById(matchId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            matchService.update(matchDTO);
        });

        assertEquals("Match not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete Match successfully")
    void deleteCaseSuccess() {
        long matchId = 1;

        when(matchRepository.existsById(matchId)).thenReturn(true);

        String response = matchService.delete(matchId);

        verify(matchRepository, times(1)).deleteById(matchId);

        assertEquals("", response);
    }

    @Test
    @DisplayName("Should throw NotFoundException: Match not found")
    void deleteCaseMatchDoesNotExist() {
        long matchId = 1;

        when(matchRepository.existsById(matchId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            matchService.delete(matchId);
        });

        assertEquals("Match not found", exception.getMessage());
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

        MatchDTO response = matchService.get(matchId);

        assertNotNull(response);
        assertEquals(matchId, response.getId());
    }

    @Test
    @DisplayName("Should throw NotFoundException: Match not found")
    void getCaseMatchDoesNotExist() {
        long matchId = 1;

        when(matchRepository.existsById(matchId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            matchService.delete(matchId);
        });

        assertEquals("Match not found", exception.getMessage());
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

        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "id"));
        when(matchRepository.findAll(pageable)).thenReturn(new PageImpl<Match>(matches));

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", false);

        assertNotNull(response);
        assertEquals(matchId, response.getFirst().getId());
    }

    @Test
    @DisplayName("Should get all home matches successfully")
    void listByTeamAndHomeMatchesCaseSuccess() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        Long homeMatchId = 1L;
        Match match = new Match(homeMatchId, 1, 0, LocalDateTime.of(2000, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2000, 1, 3, 10, 10, 10), team2, team1, stadium);

        List<Match> team1HomeMatches = new ArrayList<>();
        team1HomeMatches.add(match);

        List<Match> team1AwayMatches = new ArrayList<>();
        team1AwayMatches.add(match2);

        team1.setHomeMatches(team1HomeMatches);
        team1.setAwayMatches(team1AwayMatches);

        when(matchRepository.findAllByHomeTeamId(1L)).thenReturn(team1HomeMatches);

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", team1.getId(), "home", false);

        assertNotNull(response);
        assertEquals(homeMatchId, response.getFirst().getId());
    }

    @Test
    @DisplayName("Should get all away matches successfully")
    void listByTeamAndAwayMatchesCaseSuccess() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        Long awayMatchId = 2L;
        Match match = new Match(1L, 1, 0, LocalDateTime.of(2000, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(awayMatchId, 0, 0, LocalDateTime.of(2000, 1, 3, 10, 10, 10), team2, team1, stadium);

        List<Match> team1HomeMatches = new ArrayList<>();
        team1HomeMatches.add(match);

        List<Match> team1AwayMatches = new ArrayList<>();
        team1AwayMatches.add(match2);

        team1.setHomeMatches(team1HomeMatches);
        team1.setAwayMatches(team1AwayMatches);

        when(matchRepository.findAllByAwayTeamId(1L)).thenReturn(team1AwayMatches);

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", team1.getId(), "away", false);

        assertNotNull(response);
        assertEquals(awayMatchId, response.getFirst().getId());
    }

    @Test
    @DisplayName("Should get all team matches successfully")
    void listByTeamCaseSuccess() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        Long homeMatchId = 1L;
        Match match = new Match(homeMatchId, 1, 0, LocalDateTime.of(2000, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2000, 1, 3, 10, 10, 10), team2, team1, stadium);

        List<Match> team1HomeMatches = new ArrayList<>();
        team1HomeMatches.add(match);

        List<Match> team1AwayMatches = new ArrayList<>();
        team1AwayMatches.add(match2);

        team1.setHomeMatches(team1HomeMatches);
        team1.setAwayMatches(team1AwayMatches);

        List<Match> team1AllMatches = new ArrayList<>();
        team1AllMatches.add(match);

        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "id"));

        team1AllMatches.add(match2);

        when(matchRepository.findAllByHomeTeamIdOrAwayTeamId(1L, 1L, pageable)).thenReturn(team1AllMatches);

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", team1.getId(), null, false);

        assertNotNull(response);
        assertEquals(homeMatchId, response.getFirst().getId());
    }

    @Test
    @DisplayName("Should get all stadium matches successfully")
    void listByStadiumCaseSuccess() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Long stadiumId = 1L;
        Stadium stadium = new Stadium(stadiumId, "Morumbi", null, null);
        Match match = new Match(1L, 1, 0, LocalDateTime.of(2000, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2000, 1, 3, 10, 10, 10), team2, team1, stadium);

        List<Match> matches = new ArrayList<>();
        matches.add(match);
        matches.add(match2);

        stadium.setMatches(matches);

        Pageable page = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "id"));
        when(matchRepository.findAllByStadiumId(stadiumId, page)).thenReturn(matches);

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", stadiumId, false);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should get all not hammering matches successfully")
    void listNotHammeringCaseSuccess() {
        Team team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);
        Team team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        Stadium stadium = new Stadium(1L, "Morumbi", null, null);
        Long matchId = 1L;
        List<Match> matches = new ArrayList<>();
        Match match = new Match(matchId, 1, 0, LocalDateTime.of(2000, 1, 2, 10, 10, 10), team1, team2, stadium);
        Match match2 = new Match(2L, 0, 0, LocalDateTime.of(2000, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match);
        matches.add(match2);

        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "id"));
        when(matchRepository.findAll(pageable)).thenReturn(new PageImpl<Match>(matches));

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", false);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should get all hammering matches successfully")
    void listHammeringCaseSuccess() {
        Match match1 = new Match();
        match1.setHomeGoals(5);
        match1.setAwayGoals(0);

        Match match2 = new Match();
        match2.setHomeGoals(2);
        match2.setAwayGoals(1);

        Match match3 = new Match();
        match3.setHomeGoals(4);
        match3.setAwayGoals(1);

        when(matchRepository.findAllByHomeGoalsNotNullOrAwayGoalsNotNull())
                .thenReturn(Arrays.asList(match1, match2, match3));

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", true);

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get all matches successfully paginated and sorted")
    void listWithPaginationAndSortCaseSuccess() {
        Match match1 = new Match();
        match1.setHomeGoals(3);
        match1.setAwayGoals(1);

        Match match2 = new Match();
        match2.setHomeGoals(2);
        match2.setAwayGoals(2);

        Match match3 = new Match();
        match3.setHomeGoals(4);
        match3.setAwayGoals(0);

        List<Match> matches = Arrays.asList(match1, match2, match3);
        Page<Match> pageMatches = new PageImpl<>(matches);

        when(matchRepository.findAll(any(Pageable.class))).thenReturn(pageMatches);

        int page = 0;
        int itemsPerPage = 10;
        String sort = "goals,asc";

        List<MatchDTO> response = matchService.list(page, itemsPerPage, sort, false);

        assertNotNull(response);
        assertEquals(3, response.size());
    }
}
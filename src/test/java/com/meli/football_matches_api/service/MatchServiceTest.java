package com.meli.football_matches_api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.MatchRepository;
import com.meli.football_matches_api.repository.StadiumRepository;
import com.meli.football_matches_api.repository.TeamRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @BeforeEach
    void setUp() {}

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

        ResponseEntity<MatchDTO> response = matchService.create(matchDTO);

        Assertions.assertEquals(201, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(matchId, response.getBody().getId());
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

        long matchId = 3L;
        Match newMatch = new Match(matchId, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        FieldException thrown = Assertions.assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        Assertions.assertEquals("[homeTeamId] cannot be null", thrown.getMessage());
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

        long matchId = 3L;
        Match newMatch = new Match(matchId, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        FieldException thrown = Assertions.assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        Assertions.assertEquals("[awayTeamId] cannot be null", thrown.getMessage());
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

        long matchId = 3L;
        Match newMatch = new Match(matchId, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team1, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        FieldException thrown = Assertions.assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        Assertions.assertEquals("[homeTeam] and [awayTeam] cannot be the same", thrown.getMessage());
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

        long matchId = 3L;
        Match newMatch = new Match(matchId, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(null);

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            matchService.create(matchDTO);
        });

        Assertions.assertEquals("homeTeam not found", thrown.getMessage());
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

        long matchId = 3L;
        Match newMatch = new Match(matchId, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(null);

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            matchService.create(matchDTO);
        });

        Assertions.assertEquals("awayTeam not found", thrown.getMessage());
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

        long matchId = 3L;
        Match newMatch = new Match(matchId, null, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        FieldException thrown = Assertions.assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        Assertions.assertEquals("[homeGoals] cannot be null", thrown.getMessage());
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

        long matchId = 3L;
        Match newMatch = new Match(matchId, 1, null, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        FieldException thrown = Assertions.assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        Assertions.assertEquals("[awayGoals] cannot be null", thrown.getMessage());
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

        long matchId = 3L;
        Match newMatch = new Match(matchId, -1, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        FieldException thrown = Assertions.assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        Assertions.assertEquals("[homeGoals] cannot be negative", thrown.getMessage());
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

        long matchId = 3L;
        Match newMatch = new Match(matchId, 1, -1, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        MatchDTO matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(team1);
        when(teamRepository.findById(2L)).thenReturn(team2);

        FieldException thrown = Assertions.assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        Assertions.assertEquals("[awayGoals] cannot be negative", thrown.getMessage());
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

        ResponseEntity<MatchDTO> response = matchService.get(matchId);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(matchId, response.getBody().getId());
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

        when(matchRepository.findAll()).thenReturn(matches);

        ResponseEntity<List<MatchDTO>> response = matchService.list();

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(matchId, response.getBody().getFirst().getId());
    }
}
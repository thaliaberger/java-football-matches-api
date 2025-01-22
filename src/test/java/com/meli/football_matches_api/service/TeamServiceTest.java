package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.RetrospectDTO;
import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.TeamRepository;
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
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TeamServiceTest {

    @Mock
    private TeamRepository repository;

    @InjectMocks
    private TeamService teamService;

    long teamId = 1L;
    Team team1 = new Team();
    Team team2 = new Team();
    Team team3 = new Team();
    TeamDTO teamDTO = new TeamDTO();
    List<Match> team1HomeMatches = new ArrayList<>();
    List<Match> team1AwayMatches = new ArrayList<>();
    List<Team> teamsToBeRanked = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        teamsToBeRanked.add(team3);
        teamsToBeRanked.add(team2);
        teamsToBeRanked.add(team1);
    }

    @Test
    @DisplayName("Should create Team successfully")
    void createCaseSuccess() {
        when(repository.save(any(Team.class))).thenReturn(new Team(teamDTO));
        TeamDTO response = teamService.create(teamDTO);

        assertNotNull(response);
        assertNotNull(response);
        assertEquals(teamDTO.getName(), response.getName());
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
        TeamDTO newTeamDTO = new TeamDTO(2L, "Flamengo", "RJ", LocalDate.of(1980, 1, 1), true);

        when(repository.findByNameAndState(newTeamDTO.getName(), newTeamDTO.getState())).thenReturn(new Team(teamDTO));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            teamService.create(newTeamDTO);
        });

        assertEquals("Already existing team with name [Flamengo] and state [RJ]", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when dateCreated is null")
    void createCaseDateCreatedNull() {
        TeamDTO newTeamDTO = new TeamDTO(2L, "Flamengo", "RJ", null, true);

        when(repository.findByNameAndState(newTeamDTO.getName(), newTeamDTO.getState())).thenReturn(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            teamService.create(newTeamDTO);
        });

        assertEquals("[dateCreated] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when dateCreated is in the future")
    void createCaseDateCreatedInTheFuture() {
        int nextYear = Year.now().getValue() + 1;
        TeamDTO newTeamDTO = new TeamDTO(2L, "Flamengo", "RJ", LocalDate.of(nextYear, 1, 1), true);

        when(repository.findByNameAndState(newTeamDTO.getName(), newTeamDTO.getState())).thenReturn(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            teamService.create(newTeamDTO);
        });

        assertEquals("[dateCreated] cannot be in the future", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when state is null or empty")
    void createCaseStateIsNullOrEmpty() {
        TeamDTO teamDTO = new TeamDTO(2L, "Flamengo", "", LocalDate.of(2000, 1, 1), true);

        when(repository.findByNameAndState(teamDTO.getName(), teamDTO.getState())).thenReturn(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            teamService.create(teamDTO);
        });

        assertEquals("[state] cannot be empty or null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when state length is different than 2")
    void createCaseStateLengthDifferentThan2() {
        TeamDTO teamDTO = new TeamDTO(2L, "Flamengo", "Rio de Janeiro", LocalDate.of(2000, 1, 1), true);

        when(repository.findByNameAndState(teamDTO.getName(), teamDTO.getState())).thenReturn(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            teamService.create(teamDTO);
        });

        assertEquals("[state] must contain 2 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when state is invalid")
    void createCaseInvalidState() {
        TeamDTO teamDTO = new TeamDTO(2L, "Flamengo", "SS", LocalDate.of(2000, 1, 1), true);

        when(repository.findByNameAndState(teamDTO.getName(), teamDTO.getState())).thenReturn(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            teamService.create(teamDTO);
        });

        assertEquals("[state] is not a valid", exception.getMessage());
    }

    @Test
    @DisplayName("Should update Team successfully")
    void updateCaseSuccess() {
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(any(Team.class))).thenReturn(new Team(teamDTO));

        TeamDTO response = teamService.update(teamDTO);

        assertNotNull(response);
        assertEquals(teamDTO.getName(), response.getName());
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating Team that does not exist")
    void updateCaseTeamDoesNotExist() {
        when(repository.findById(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> teamService.update(teamDTO));
    }

    @Test
    @DisplayName("Should throw ConflictException when dateCreated is after match date")
    void updateCaseDateCreatedIsAfterMatchDate() {
        long teamId = 2L;
        TeamDTO teamDTO = new TeamDTO(teamId, "Flamengo", "RJ", LocalDate.of(2025, 1, 1), true);
        LocalDateTime dateTime = teamDTO.getDateCreated().atTime(0,0);

        when(repository.existsById(teamId)).thenReturn(true);
        when(repository.existsByIdAndHomeMatchesMatchDateTimeBefore(teamDTO.getId(), dateTime)).thenReturn(true);
        when(repository.existsByIdAndAwayMatchesMatchDateTimeBefore(teamDTO.getId(), dateTime)).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            teamService.update(teamDTO);
        });

        assertEquals("[dateCreated] cannot be after match date", exception.getMessage());
    }

    @Test
    @DisplayName("Should inactivate Team")
    void deleteCaseSuccess() {
        when(repository.findById(1L)).thenReturn(team1);

        String response = teamService.delete(1L);

        assertEquals( "", response);
        assertEquals(false, team1.getIsActive());

        verify(repository, times(1)).save(team1);
    }

    @Test
    @DisplayName("Should throw NotFoundException")
    void deleteCaseTeamNotFound() {
        when(repository.findById(1L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            teamService.delete(1L);
        });

        assertEquals("Team not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get Team successfully")
    void getCaseSuccess() {
        when(repository.findById(teamId)).thenReturn(team1);

        TeamDTO response = teamService.get(teamId);

        assertNotNull(response);
        assertEquals(teamDTO.getName(), response.getName());
    }

    @Test
    @DisplayName("Should get Team list successfully")
    void listCaseSuccess() {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "id"));
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<>(teamsToBeRanked));

        List<TeamDTO> response = teamService.list(0, 1000, "id,asc", null, null, null);

        assertNotNull(response);
        assertEquals(3L, response.getFirst().getId());
        assertEquals(1L, response.getLast().getId());
        assertEquals(3, response.size());
    }

    @Test
    @DisplayName("Should throw NotFoundException")
    void getCaseTeamNotFound() {
        when(repository.findById(teamId)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            teamService.get(teamId);
        });

        assertEquals("Team not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get Team retrospect successfully")
    void getRetrospectCaseSuccess() {
        when(repository.findById(teamId)).thenReturn(team1);

        RetrospectDTO response = teamService.getRetrospect(teamId, "", false);

        assertNotNull(response);
        assertEquals(1, response.getWins());
        assertEquals(1, response.getDraws());
        assertEquals(1, response.getLosses());
        assertEquals(5, response.getScoredGoals());
        assertEquals(2, response.getConcededGoals());
        assertEquals(4, response.getScore());
        assertEquals(3, response.getMatches().size());
    }

    @Test
    @DisplayName("Should get Team retrospect against opponent successfully")
    void getRetrospectAgainstOpponentCaseSuccess() {
        when(repository.findById(teamId)).thenReturn(team1);
        when(repository.findById(3L)).thenReturn(team3);

        RetrospectDTO response = teamService.getRetrospect(teamId, 3L, "", false);

        assertNotNull(response);
        assertEquals(0, response.getWins());
        assertEquals(0, response.getDraws());
        assertEquals(1, response.getLosses());
        assertEquals(1, response.getScoredGoals());
        assertEquals(2, response.getConcededGoals());
        assertEquals(0, response.getScore());
        assertEquals(1, response.getMatches().size());
    }

    @Test
    @DisplayName("Should throw NotFoundException")
    void getRetrospectAgainstOpponentCaseOpponentNotFound() {
        when(repository.findById(teamId)).thenReturn(team1);
        when(repository.findById(3L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            teamService.getRetrospect(teamId, 3L, "", false);
        });

        assertEquals("Opponent team not found", exception.getMessage());

    }

    @Test
    @DisplayName("Should get Team home matches retrospect successfully")
    void getTeamHomeMatchesRetrospectCaseSuccess() {
        when(repository.findById(teamId)).thenReturn(team1);

        RetrospectDTO response = teamService.getRetrospect(teamId,  "home", false);

        assertNotNull(response);
        assertEquals(1, response.getWins());
        assertEquals(1, response.getDraws());
        assertEquals(0, response.getLosses());
        assertEquals(4, response.getScoredGoals());
        assertEquals(0, response.getConcededGoals());
        assertEquals(4, response.getScore());
        assertEquals(2, response.getMatches().size());
    }

    @Test
    @DisplayName("Should get Team away matches retrospect successfully")
    void getTeamAwayMatchesRetrospectCaseSuccess() {
        when(repository.findById(teamId)).thenReturn(team1);

        RetrospectDTO response = teamService.getRetrospect(teamId,  "away", false);

        assertNotNull(response);
        assertEquals(0, response.getWins());
        assertEquals(0, response.getDraws());
        assertEquals(1, response.getLosses());
        assertEquals(1, response.getScoredGoals());
        assertEquals(2, response.getConcededGoals());
        assertEquals(0, response.getScore());
        assertEquals(1, response.getMatches().size());
    }

    @Test
    @DisplayName("Should get Team hammering matches retrospect successfully")
    void getHammeringMatchesRetrospectCaseSuccess() {
        when(repository.findById(teamId)).thenReturn(team1);

        RetrospectDTO response = teamService.getRetrospect(teamId,  "", true);

        assertNotNull(response);
        assertEquals(1, response.getWins());
        assertEquals(0, response.getDraws());
        assertEquals(0, response.getLosses());
        assertEquals(4, response.getScoredGoals());
        assertEquals(0, response.getConcededGoals());
        assertEquals(3, response.getScore());
        assertEquals(1, response.getMatches().size());
    }

    @Test
    @DisplayName("Should get Team retrospect against all successfully")
    void getRetrospectAgainstAllCaseSuccess() {
        when(repository.findById(teamId)).thenReturn(team1);

        HashMap<String, RetrospectDTO> response = teamService.getRetrospectAgainstAll(teamId);

        assertNotNull(response);
        assertEquals(0, response.get("Figueirense").getWins());
        assertEquals(1, response.get("Fluminense").getWins());
    }

    @Test
    @DisplayName("Should throw NotFoundException")
    void getRetrospectAgainstAllCaseTeamNotFound() {
        when(repository.findById(teamId)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            teamService.getRetrospectAgainstAll(teamId);
        });

        assertEquals("Team not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get Teams ranked by goals successfully")
    void getRankingByGoalsCaseSuccess() {
        when(repository.findByHomeMatchesNotNullOrAwayMatchesNotNull()).thenReturn(teamsToBeRanked);

        List<TeamDTO> response = teamService.ranking("goals", null);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(3L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by matches successfully")
    void getRankingByMatchesCaseSuccess() {
        when(repository.findByHomeMatchesNotNullOrAwayMatchesNotNull()).thenReturn(teamsToBeRanked);

        List<TeamDTO> response = teamService.ranking("matches", null);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(3L, response.getLast().getId());
        assertEquals(3, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by score successfully")
    void getRankingByScoreCaseSuccess() {
        when(repository.findByHomeMatchesNotNullOrAwayMatchesNotNull()).thenReturn(teamsToBeRanked);

        List<TeamDTO> response = teamService.ranking("score", null);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(2L, response.getLast().getId());
        assertEquals(3, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by wins successfully")
    void getRankingByWinsCaseSuccess() {
        when(repository.findByHomeMatchesNotNullOrAwayMatchesNotNull()).thenReturn(teamsToBeRanked);

        List<TeamDTO> response = teamService.ranking("wins", null);

        assertNotNull(response);
        assertEquals(3L, response.getFirst().getId());
        assertEquals(1L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by home wins successfully")
    void getRankingByHomeWinsCaseSuccess() {
        when(repository.findByHomeMatchesHomeGoalsNotNull()).thenReturn(teamsToBeRanked);

        List<TeamDTO> response = teamService.ranking("wins", "home");

        assertNotNull(response);
        assertEquals(3L, response.getFirst().getId());
        assertEquals(1L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by away wins successfully")
    void getRankingByAwayWinsCaseSuccess() {
        when(repository.findByAwayMatchesHomeGoalsNotNull()).thenReturn(teamsToBeRanked);

        List<TeamDTO> response = teamService.ranking("wins", "away");

        assertNotNull(response);
        assertEquals(3L, response.getFirst().getId());
        assertEquals(1L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by home goals successfully")
    void getRankingByHomeGoalsCaseSuccess() {
        when(repository.findByHomeMatchesHomeGoalsNotNull()).thenReturn(teamsToBeRanked);

        List<TeamDTO> response = teamService.ranking("goals", "home");

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(3L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by away goals successfully")
    void getRankingByAwayGoalsCaseSuccess() {
        when(repository.findByAwayMatchesHomeGoalsNotNull()).thenReturn(teamsToBeRanked);

        List<TeamDTO> response = teamService.ranking("goals", "away");

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(3L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by home score successfully")
    void getRankingByHomeScoreCaseSuccess() {
        teamsToBeRanked.remove(team2);
        when(repository.findByHomeMatchesNotNull()).thenReturn(teamsToBeRanked);

        List<TeamDTO> response = teamService.ranking("score", "home");

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(3L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by away score successfully")
    void getRankingByAwayScoreCaseSuccess() {
        teamsToBeRanked.remove(team3);
        when(repository.findByAwayMatchesNotNull()).thenReturn(teamsToBeRanked);

        List<TeamDTO> response = teamService.ranking("score", "away");

        assertNotNull(response);
        assertEquals(2L, response.getFirst().getId());
        assertEquals(1L, response.getLast().getId());
        assertEquals(2, response.size());
    }
}
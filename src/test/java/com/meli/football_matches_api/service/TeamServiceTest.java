package com.meli.football_matches_api.service;

import com.meli.football_matches_api.dto.RetrospectDTO;
import com.meli.football_matches_api.dto.TeamDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.ranking.RankingGenerator;
import com.meli.football_matches_api.ranking.goals.RankByAwayGoals;
import com.meli.football_matches_api.ranking.goals.RankByGoals;
import com.meli.football_matches_api.ranking.goals.RankByHomeGoals;
import com.meli.football_matches_api.ranking.matches.RankByAwayMatches;
import com.meli.football_matches_api.ranking.matches.RankByHomeMatches;
import com.meli.football_matches_api.ranking.matches.RankByMatches;
import com.meli.football_matches_api.ranking.score.RankByAwayScore;
import com.meli.football_matches_api.ranking.score.RankByHomeScore;
import com.meli.football_matches_api.ranking.score.RankByScore;
import com.meli.football_matches_api.ranking.wins.RankByAwayWins;
import com.meli.football_matches_api.ranking.wins.RankByHomeWins;
import com.meli.football_matches_api.ranking.wins.RankByWins;
import com.meli.football_matches_api.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TeamServiceTest {

    @Mock
    private TeamRepository repository;

    @Mock
    private RankingGenerator rankingGenerator;

    @Mock
    private RankByHomeScore rankByHomeScore;
    @Mock
    private RankByAwayScore rankByAwayScore;
    @Mock
    private RankByScore rankByScore;

    @Mock
    private RankByAwayGoals rankByAwayGoals;
    @Mock
    private RankByHomeGoals rankByHomeGoals;
    @Mock
    private RankByGoals rankByGoals;

    @Mock
    private RankByAwayMatches rankByAwayMatches;
    @Mock
    private RankByHomeMatches rankByHomeMatches;
    @Mock
    private RankByMatches rankByMatches;

    @Mock
    private RankByAwayWins rankByAwayWins;
    @Mock
    private RankByHomeWins rankByHomeWins;
    @Mock
    private RankByWins rankByWins;

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
    List<TeamDTO> ranking;
    Pageable pageable;

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

        ranking = new ArrayList<>();

        pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "id"));
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
        teamDTO.setName("");

        FieldException exception = assertThrows(FieldException.class, () -> {
            teamService.create(teamDTO);
        });

        assertEquals("[name] cannot be empty or null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when Team isActive is null")
    void createCaseIsActiveNull() {
        teamDTO.setIsActive(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            teamService.create(teamDTO);
        });

        assertEquals("[isActive] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException when Team already exists")
    void createCaseTeamAlreadyExists() {
        when(repository.findByNameAndState(teamDTO.getName(), teamDTO.getState())).thenReturn(new Team(teamDTO));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            teamService.create(teamDTO);
        });

        assertEquals("Already existing team with name [Flamengo] and state [RJ]", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when dateCreated is null")
    void createCaseDateCreatedNull() {
        teamDTO.setDateCreated(null);

        when(repository.findByNameAndState(teamDTO.getName(), teamDTO.getState())).thenReturn(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            teamService.create(teamDTO);
        });

        assertEquals("[dateCreated] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException when dateCreated is in the future")
    void createCaseDateCreatedInTheFuture() {
        int nextYear = Year.now().getValue() + 1;
        teamDTO.setDateCreated(LocalDate.of(nextYear, 1, 1));

        when(repository.findByNameAndState(teamDTO.getName(), teamDTO.getState())).thenReturn(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            teamService.create(teamDTO);
        });

        assertEquals("[dateCreated] cannot be in the future", exception.getMessage());
    }

    static Stream<CaseTestInvalidState> createCaseStateParamProvider() {
        return Stream.of(
                new CaseTestInvalidState(null, "[state] cannot be empty or null"),
                new CaseTestInvalidState("Rio de Janeiro", "[state] must contain 2 characters"),
                new CaseTestInvalidState("SO", "[state] is not a valid")
        );
    }

    @ParameterizedTest
    @MethodSource("createCaseStateParamProvider")
    @DisplayName("Should throw FieldException for invalid state")
    void createCaseStateInvalid(CaseTestInvalidState testData) {
        teamDTO.setState(testData.state);

        when(repository.findByNameAndState(teamDTO.getName(), teamDTO.getState())).thenReturn(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            teamService.create(teamDTO);
        });

        assertEquals(testData.expectedMessage, exception.getMessage());
    }

    private record CaseTestInvalidState(String state, String expectedMessage) {}

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
        teamId = 2L;
        team2.setDateCreated(LocalDate.of(2025, 1, 1));
        teamDTO = new TeamDTO(team2);
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
        when(repository.findById(teamId)).thenReturn(Optional.ofNullable(team1));

        String response = teamService.delete(teamId);

        assertEquals( "", response);
        assertEquals(false, team1.getIsActive());

        verify(repository, times(1)).save(team1);
    }

    @Test
    @DisplayName("Should throw NotFoundException")
    void deleteCaseTeamNotFound() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            teamService.delete(5L);
        });

        assertEquals("Team not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get Team successfully")
    void getCaseSuccess() {
        when(repository.findById(teamId)).thenReturn(Optional.ofNullable(team1));

        TeamDTO response = teamService.get(teamId);

        assertNotNull(response);
        assertEquals(teamDTO.getName(), response.getName());
    }

    @Test
    @DisplayName("Should get Team list successfully")
    void listCaseSuccess() {
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<>(teamsToBeRanked));

        List<TeamDTO> response = teamService.list(0, 1000, "id,asc", null, null, null);

        assertNotNull(response);
        assertEquals(3L, response.getFirst().getId());
        assertEquals(1L, response.getLast().getId());
        assertEquals(3, response.size());
    }

    @Test
    @DisplayName("Should get list of teams by name successfully")
    void listByName() {
        List<Team> teamList = new ArrayList<>();
        teamList.add(team1);
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<>(teamList));

        List<TeamDTO> response = teamService.list(0, 1000, "id,asc", "Flamengo", null, null);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(1, response.size());
        assertEquals("Flamengo", response.getFirst().getName());
    }

    @Test
    @DisplayName("Should get list of teams by state successfully")
    void listByState() {
        List<Team> teamList = new ArrayList<>();
        teamList.add(team1);
        teamList.add(team2);
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<>(teamList));

        List<TeamDTO> response = teamService.list(0, 1000, "id,asc", null , "RJ", null);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(2, response.size());
        assertEquals("Flamengo", response.getFirst().getName());
    }

    @Test
    @DisplayName("Should throw NotFoundException")
    void getCaseTeamNotFound() {
        when(repository.findById(teamId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            teamService.get(teamId);
        });

        assertEquals("Team not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get Team retrospect successfully")
    void getRetrospectCaseSuccess() {
        when(repository.findById(teamId)).thenReturn(Optional.ofNullable(team1));

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
        when(repository.findById(teamId)).thenReturn(Optional.ofNullable(team1));
        when(repository.findById(3L)).thenReturn(Optional.ofNullable(team3));

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
        when(repository.findById(teamId)).thenReturn(Optional.ofNullable(team1));
        when(repository.findById(3L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            teamService.getRetrospect(teamId, 3L, "", false);
        });

        assertEquals("Opponent team not found", exception.getMessage());

    }

    @Test
    @DisplayName("Should get Team home matches retrospect successfully")
    void getTeamHomeMatchesRetrospectCaseSuccess() {
        when(repository.findById(teamId)).thenReturn(Optional.ofNullable(team1));

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
        when(repository.findById(teamId)).thenReturn(Optional.ofNullable(team1));

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
        when(repository.findById(teamId)).thenReturn(Optional.ofNullable(team1));

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
        when(repository.findById(teamId)).thenReturn(Optional.ofNullable(team1));

        Map<String, RetrospectDTO> response = teamService.getRetrospectAgainstAll(teamId);

        assertNotNull(response);
        assertEquals(0, response.get("Figueirense").getWins());
        assertEquals(1, response.get("Fluminense").getWins());
    }

    @Test
    @DisplayName("Should throw NotFoundException")
    void getRetrospectAgainstAllCaseTeamNotFound() {
        when(repository.findById(teamId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            teamService.getRetrospectAgainstAll(teamId);
        });

        assertEquals("Team not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get Teams ranked by goals successfully")
    void getRankingByGoalsCaseSuccess() {
        String rankBy = "goals";
        ranking.add(teamDTO);
        ranking.add(new TeamDTO(team3));

        when(rankingGenerator.createGenerator(rankBy)).thenReturn(rankByGoals);
        when(rankByGoals.execute()).thenReturn(ranking);

        List<TeamDTO> response = teamService.ranking(rankBy, null);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(3L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by matches successfully")
    void getRankingByMatchesCaseSuccess() {
        String rankBy = "matches";
        ranking.add(teamDTO);
        ranking.add(new TeamDTO(team2));
        ranking.add(new TeamDTO(team3));

        when(rankingGenerator.createGenerator(rankBy)).thenReturn(rankByMatches);
        when(rankByMatches.execute()).thenReturn(ranking);

        List<TeamDTO> response = teamService.ranking(rankBy, null);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(3L, response.getLast().getId());
        assertEquals(3, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by home matches successfully")
    void getRankingByHomeMatchesCaseSuccess() {
        String rankBy = "matches";
        String matchLocation = "home";
        ranking.add(teamDTO);
        ranking.add(new TeamDTO(team3));

        when(rankingGenerator.createGenerator(rankBy, matchLocation)).thenReturn(rankByHomeMatches);
        when(rankByHomeMatches.execute()).thenReturn(ranking);

        List<TeamDTO> response = teamService.ranking(rankBy, matchLocation);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(3L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by away matches successfully")
    void getRankingByAwayMatchesCaseSuccess() {
        String rankBy = "matches";
        String matchLocation = "away";
        ranking.add(new TeamDTO(team2));
        ranking.add(teamDTO);

        when(rankingGenerator.createGenerator(rankBy, matchLocation)).thenReturn(rankByAwayMatches);
        when(rankByAwayMatches.execute()).thenReturn(ranking);

        List<TeamDTO> response = teamService.ranking(rankBy, matchLocation);

        assertNotNull(response);
        assertEquals(2L, response.getFirst().getId());
        assertEquals(1L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by score successfully")
    void getRankingByScoreCaseSuccess() {
        String rankBy = "score";
        ranking.add(teamDTO);
        ranking.add(new TeamDTO(team3));
        ranking.add(new TeamDTO(team2));

        when(rankingGenerator.createGenerator(rankBy)).thenReturn(rankByScore);
        when(rankByScore.execute()).thenReturn(ranking);

        List<TeamDTO> response = teamService.ranking(rankBy, null);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(2L, response.getLast().getId());
        assertEquals(3, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by wins successfully")
    void getRankingByWinsCaseSuccess() {
        String rankBy = "wins";
        ranking.add(new TeamDTO(team3));
        ranking.add(teamDTO);

        when(rankingGenerator.createGenerator(rankBy)).thenReturn(rankByWins);
        when(rankByWins.execute()).thenReturn(ranking);

        List<TeamDTO> response = teamService.ranking(rankBy, null);

        assertNotNull(response);
        assertEquals(3L, response.getFirst().getId());
        assertEquals(1L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by home wins successfully")
    void getRankingByHomeWinsCaseSuccess() {
        String rankBy = "wins";
        String matchLocation = "home";
        ranking.add(new TeamDTO(team3));
        ranking.add(teamDTO);

        when(rankingGenerator.createGenerator(rankBy, matchLocation)).thenReturn(rankByHomeWins);
        when(rankByHomeWins.execute()).thenReturn(ranking);

        List<TeamDTO> response = teamService.ranking(rankBy, matchLocation);

        assertNotNull(response);
        assertEquals(3L, response.getFirst().getId());
        assertEquals(1L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by away wins successfully")
    void getRankingByAwayWinsCaseSuccess() {
        String rankBy = "wins";
        String matchLocation = "away";
        ranking.add(new TeamDTO(team3));
        ranking.add(teamDTO);

        when(rankingGenerator.createGenerator(rankBy, matchLocation)).thenReturn(rankByAwayWins);
        when(rankByAwayWins.execute()).thenReturn(ranking);

        List<TeamDTO> response = teamService.ranking(rankBy, matchLocation);

        assertNotNull(response);
        assertEquals(3L, response.getFirst().getId());
        assertEquals(1L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by home goals successfully")
    void getRankingByHomeGoalsCaseSuccess() {
        String rankBy = "goals";
        String matchLocation = "home";
        ranking.add(teamDTO);
        ranking.add(new TeamDTO(team3));

        when(rankingGenerator.createGenerator(rankBy, matchLocation)).thenReturn(rankByHomeGoals);
        when(rankByHomeGoals.execute()).thenReturn(ranking);

        List<TeamDTO> response = teamService.ranking(rankBy, matchLocation);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(3L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by away goals successfully")
    void getRankingByAwayGoalsCaseSuccess() {
        String rankBy = "goals";
        String matchLocation = "away";
        ranking.add(teamDTO);
        ranking.add(new TeamDTO(team3));

        when(rankingGenerator.createGenerator(rankBy, matchLocation)).thenReturn(rankByAwayGoals);
        when(rankByAwayGoals.execute()).thenReturn(ranking);

        List<TeamDTO> response = teamService.ranking(rankBy, matchLocation);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(3L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by home score successfully")
    void getRankingByHomeScoreCaseSuccess() {
        String rankBy = "score";
        String matchLocation = "home";
        ranking.add(teamDTO);
        ranking.add(new TeamDTO(team3));

        when(rankingGenerator.createGenerator(rankBy, matchLocation)).thenReturn(rankByHomeScore);
        when(rankByHomeScore.execute()).thenReturn(ranking);

        List<TeamDTO> response = teamService.ranking(rankBy, matchLocation);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
        assertEquals(3L, response.getLast().getId());
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get Teams ranked by away score successfully")
    void getRankingByAwayScoreCaseSuccess() {
        String rankBy = "score";
        String matchLocation = "away";
        ranking.add(new TeamDTO(team2));
        ranking.add(teamDTO);

        when(rankingGenerator.createGenerator(rankBy, matchLocation)).thenReturn(rankByAwayScore);
        when(rankByAwayScore.execute()).thenReturn(ranking);

        List<TeamDTO> response = teamService.ranking(rankBy, matchLocation);

        assertNotNull(response);
        assertEquals(2L, response.getFirst().getId());
        assertEquals(1L, response.getLast().getId());
        assertEquals(2, response.size());
    }
}
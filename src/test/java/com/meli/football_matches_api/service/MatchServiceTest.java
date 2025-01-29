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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    Team team1 = new Team();
    Team team2 = new Team();
    Stadium stadium = new Stadium();
    List<Match> matches = new ArrayList<>();
    Match match1 = new Match();
    Match match2 = new Match();
    Match newMatch = new Match();
    MatchDTO matchDTO = new MatchDTO();
    long newMatchId = 3;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        team1 = new Team(1L, "Flamengo", "RJ", LocalDate.of(1970, 1, 1), true);
        team2 = new Team(2L, "Fluminense", "RJ", LocalDate.of(1980, 1, 1), true);
        stadium = new Stadium(1L, "Morumbi", null, null);
        match1 = new Match(1L, 1, 0, LocalDateTime.of(2024, 1, 2, 10, 10, 10), team1, team2, stadium);
        match2 = new Match(2L, 0, 0, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team1, team2, stadium);
        matches.add(match1);
        matches.add(match2);
        stadium.setMatches(matches);

        newMatch = new Match(newMatchId, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        matchDTO = new MatchDTO(newMatch);

        pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "id"));
    }

    @Test
    @DisplayName("Should create Match successfully")
    void createCaseSuccess() {
        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));
        when(stadiumRepository.findById(1L)).thenReturn(Optional.ofNullable(stadium));
        when(matchRepository.save(any(Match.class))).thenReturn(new Match(matchDTO));

        MatchDTO response = matchService.create(matchDTO);

        assertNotNull(response);
        assertEquals(newMatchId, response.getId());
    }

    @Test
    @DisplayName("Should throw FieldException: [homeTeamId] cannot be null")
    void createCaseHomeTeamIdNull() {
        team1.setId(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[homeTeamId] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [awayTeamId] cannot be null")
    void createCaseAwayTeamIdNull() {
        team2.setId(null);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[awayTeamId] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [homeTeam] and [awayTeam] cannot be the same")
    void createCaseHomeTeamIdAndAwayTeamIdAreTheSame() {
        newMatch = new Match(3L, 0, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team1, stadium);
        matchDTO = new MatchDTO(newMatch);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[homeTeam] and [awayTeam] cannot be the same", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NotFoundException: homeTeam not found")
    void createCaseHomeTeamNotFound() {
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("homeTeam not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NotFoundException: awayTeam not found")
    void createCaseAwayTeamNotFound() {
        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("awayTeam not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [homeGoals] cannot be null")
    void createCaseHomeGoalsNull() {
        newMatch.setHomeGoals(null);
        matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[homeGoals] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [awayGoals] cannot be null")
    void createCaseAwayGoalsNull() {
        newMatch.setAwayGoals(null);
        matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[awayGoals] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [homeGoals] cannot be negative")
    void createCaseHomeGoalsNegative() {
        newMatch.setHomeGoals(-2);
        matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[homeGoals] cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [awayGoals] cannot be negative")
    void createCaseAwayGoalsNegative() {
        newMatch.setAwayGoals(-2);
        matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[awayGoals] cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [homeTeam] is not active")
    void createCaseHomeTeamNotActive() {
        team1.setIsActive(false);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[homeTeam] is not active", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [awayTeam] is not active")
    void createCaseAwayTeamNotActive() {
        team2.setIsActive(false);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[awayTeam] is not active", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [matchDateTime] cannot be null")
    void createCaseMatchDateTimeIsNull() {
        newMatch.setMatchDateTime(null);
        matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[matchDateTime] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [matchDateTime] cannot be in the future")
    void createCaseMatchDateTimeCannotBeInFuture() {
        int nextYear = Year.now().getValue() + 1;
        newMatch.setMatchDateTime(LocalDateTime.of(nextYear, 1, 6, 10, 10, 10));
        matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[matchDateTime] cannot be in the future", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException: [matchDateTime] cannot be before [homeTeamDateCreated]")
    void createCaseMatchDateTimeCannotBeBeforeHomeTeamDateCreated() {
        newMatch.setMatchDateTime(LocalDateTime.of((team1.getDateCreated().getYear() - 1), 1, 6, 10, 10, 10));
        matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[matchDateTime] cannot be before [homeTeamDateCreated]", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException: [matchDateTime] cannot be before [awayTeamDateCreated]")
    void createCaseMatchDateTimeCannotBeBeforeAwayTeamDateCreated() {
        newMatch.setMatchDateTime(LocalDateTime.of((team2.getDateCreated().getYear() - 1), 1, 6, 10, 10, 10));
        matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[matchDateTime] cannot be before [awayTeamDateCreated]", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException: Cannot create a match when one of the teams already has a match in less than 48 hours")
    void createCaseTeamAlreadyHasAMatchInLessThan48Hours() {
        newMatch.setStadium(new Stadium(2L, "Maracanã", null, null));
        newMatch.setMatchDateTime(match1.getMatchDateTime());
        matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));
        when(matchRepository.findAllByHomeTeam(any(Team.class))).thenReturn(matches);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("Cannot create a match when one of the teams already has a match in less than 48 hours", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [stadium] cannot be null")
    void createCaseStadiumIsNull() {
        newMatch.setStadium(null);
        matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));
        when(matchRepository.findAllByHomeTeam(any(Team.class))).thenReturn(matches);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[stadium] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FieldException: [stadium.id] cannot be null")
    void createCaseStadiumIdNull() {
        newMatch.setStadium(new Stadium(null, "Maracanã", null, null));
        matchDTO = new MatchDTO(newMatch);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));
        when(matchRepository.findAllByHomeTeam(any(Team.class))).thenReturn(matches);

        FieldException exception = assertThrows(FieldException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("[stadium.id] cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NotFoundException: Stadium not found")
    void createCaseStadiumNotFound() {
        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));
        when(matchRepository.findAllByHomeTeam(any(Team.class))).thenReturn(matches);
        when(stadiumRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("Stadium not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ConflictException: Stadium already has a match on this date")
    void createCaseStadiumAlreadyHasAMatchOnThisDate() {
        Team team3 = new Team(3L, "Avai", "SC", LocalDate.of(1980, 1, 1), true);
        Team team4 = new Team(4L, "Criciuma", "SC", LocalDate.of(1980, 1, 1), true);

        newMatch = new Match(3L, 1, 1, LocalDateTime.of(2023, 1, 3, 10, 10, 10), team3, team4, stadium);
        matchDTO = new MatchDTO(newMatch);

        List<Match> matches2 = new ArrayList<>();

        when(teamRepository.findById(3L)).thenReturn(Optional.of(team3));
        when(teamRepository.findById(4L)).thenReturn(Optional.of(team4));
        when(matchRepository.findAllByHomeTeam(any(Team.class))).thenReturn(matches2);
        when(stadiumRepository.findById(1L)).thenReturn(Optional.ofNullable(stadium));
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            matchService.create(matchDTO);
        });

        assertEquals("Stadium already has a match on this date", exception.getMessage());
    }

    @Test
    @DisplayName("Should update Match successfully")
    void updateCaseSuccess() {
        Match updatedMatch = new Match(2L, 4, 0, LocalDateTime.of(2023, 1, 6, 10, 10, 10), team1, team2, stadium);
        matchDTO = new MatchDTO(updatedMatch);

        when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(teamRepository.findById(2L)).thenReturn(Optional.ofNullable(team2));
        when(stadiumRepository.findById(1L)).thenReturn(Optional.ofNullable(stadium));
        when(matchRepository.save(any(Match.class))).thenReturn(new Match(matchDTO));

        when(matchRepository.existsById(2L)).thenReturn(true);

        MatchDTO response = matchService.update(matchDTO);

        assertNotNull(response);
        assertEquals(2L, response.getId());
    }

    @Test
    @DisplayName("Should throw NotFoundException: Match not found")
    void updateCaseMatchDoesNotExist() {
        long matchId = 3L;

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
        Long matchId = 1L;

        when(matchRepository.findById(matchId)).thenReturn(Optional.ofNullable(match1));

        MatchDTO response = matchService.get(matchId);

        assertNotNull(response);
        assertEquals(matchId, response.getId());
    }

    @Test
    @DisplayName("Should throw NotFoundException: Match not found")
    void getCaseMatchDoesNotExist() {
        long matchId = 1;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            matchService.get(matchId);
        });

        assertEquals("Match not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get all matches successfully")
    void listCaseSuccess() {
        when(matchRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<Match>(matches));

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", null, null, null, false);

        assertNotNull(response);
        assertEquals(1L, response.getFirst().getId());
    }

    @Test
    @DisplayName("Should get all home matches successfully")
    void listByTeamAndHomeMatchesCaseSuccess() {
        Long homeMatchId = 1L;

        List<Match> team1HomeMatches = new ArrayList<>();
        team1HomeMatches.add(match1);

        List<Match> team1AwayMatches = new ArrayList<>();
        team1AwayMatches.add(match2);

        team1.setHomeMatches(team1HomeMatches);
        team1.setAwayMatches(team1AwayMatches);

        when(matchRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<Match>(team1HomeMatches));

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", team1.getId(), 1L, "home", false);

        assertNotNull(response);
        assertEquals(homeMatchId, response.getFirst().getId());
    }

    @Test
    @DisplayName("Should get all away matches successfully")
    void listByTeamAndAwayMatchesCaseSuccess() {
        Long awayMatchId = 2L;

        List<Match> team1HomeMatches = new ArrayList<>();
        team1HomeMatches.add(match1);

        List<Match> team1AwayMatches = new ArrayList<>();
        team1AwayMatches.add(match2);

        team1.setHomeMatches(team1HomeMatches);
        team1.setAwayMatches(team1AwayMatches);

        when(matchRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<Match>(team1AwayMatches));

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", team1.getId(), 1L, "away", false);

        assertNotNull(response);
        assertEquals(awayMatchId, response.getFirst().getId());
    }

    @Test
    @DisplayName("Should get all team matches successfully")
    void listByTeamCaseSuccess() {
        Long homeMatchId = 1L;

        List<Match> team1HomeMatches = new ArrayList<>();
        team1HomeMatches.add(match1);

        List<Match> team1AwayMatches = new ArrayList<>();
        team1AwayMatches.add(match2);

        team1.setHomeMatches(team1HomeMatches);
        team1.setAwayMatches(team1AwayMatches);

        List<Match> team1AllMatches = new ArrayList<>();
        team1AllMatches.add(match1);
        team1AllMatches.add(match2);


        when(matchRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<Match>(team1AllMatches));

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", team1.getId(), null, null, false);

        assertNotNull(response);
        assertEquals(homeMatchId, response.getFirst().getId());
    }

    @Test
    @DisplayName("Should get all stadium matches successfully")
    void listByStadiumCaseSuccess() {
        Long stadiumId = 1L;

        when(matchRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<Match>(matches));

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc",null, stadiumId, null, false);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should get all not hammering matches successfully")
    void listNotHammeringCaseSuccess() {
        when(matchRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<Match>(matches));

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", null,null, null, false);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should get all hammering matches successfully")
    void listHammeringCaseSuccess() {
        match1.setHomeGoals(5);
        match1.setAwayGoals(0);

        match2.setHomeGoals(2);
        match2.setAwayGoals(1);

        Match match3 = new Match();
        match3.setHomeGoals(4);
        match3.setAwayGoals(1);

        when(matchRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<Match>(Arrays.asList(match1, match2, match3)));

        List<MatchDTO> response = matchService.list(0, 1000, "id,asc", null,null, null, true);

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should get all matches successfully paginated and sorted")
    void listWithPaginationAndSortCaseSuccess() {
        match1.setHomeGoals(3);
        match1.setAwayGoals(1);

        match2.setHomeGoals(2);
        match2.setAwayGoals(2);

        Match match3 = new Match();
        match3.setHomeGoals(4);
        match3.setAwayGoals(0);

        matches = Arrays.asList(match1, match2, match3);

        pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "goals"));
        when(matchRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<>(matches));

        int page = 0;
        int itemsPerPage = 10;
        String sort = "goals,asc";

        List<MatchDTO> response = matchService.list(page, itemsPerPage, sort, null, null, null, false);

        assertNotNull(response);
        assertEquals(3, response.size());
    }
}
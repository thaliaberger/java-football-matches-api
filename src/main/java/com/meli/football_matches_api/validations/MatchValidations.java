package com.meli.football_matches_api.validations;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.MatchRepository;
import com.meli.football_matches_api.repository.TeamRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MatchValidations {

    public static void validateFields(MatchDTO matchDTO, MatchRepository matchRepository, TeamRepository teamRepository) {
        Long homeTeamId = matchDTO.getIdHomeTeam();
        Long awayTeamId = matchDTO.getIdAwayTeam();

        validateIds(homeTeamId, awayTeamId);

        Team homeTeam = teamRepository.findById(homeTeamId.intValue());
        if (homeTeam == null) throw new NotFoundException("homeTeam not found");

        Team awayTeam = teamRepository.findById(awayTeamId.intValue());
        if (awayTeam == null) throw new NotFoundException("awayTeam not found");

        if (matchDTO.getIdStadium() == null) throw new FieldException("[idStadium] cannot be null");

        validateGoals(matchDTO.getHomeGoals(), matchDTO.getAwayGoals());
        validateTeams(homeTeam, awayTeam);

        LocalDateTime matchDateTime = matchDTO.getMatchDateTime();
        validateDateTime(matchDateTime, homeTeam.getDateCreated(), awayTeam.getDateCreated());

        List<Match> homeTeamMatches = matchRepository.findAllByIdAwayTeam(homeTeamId);
        validateConflictMatches(matchDateTime, homeTeamMatches);

        List<Match> awayTeamMatches = matchRepository.findAllByIdAwayTeam(awayTeamId);
        validateConflictMatches(matchDateTime, awayTeamMatches);
    };

    private static void validateGoals(Integer homeGoals, Integer awayGoals) {
        if (homeGoals == null) throw new FieldException("[homeGoals] cannot be null");
        if (awayGoals == null) throw new FieldException("[awayGoals] cannot be null");
        if (homeGoals < 0) throw new FieldException("[homeGoals] cannot be negative");
        if (awayGoals < 0) throw new FieldException("[awayGoals] cannot be negative");
    }

    private static void validateIds(Long homeTeamId, Long awayTeamId) {
        if (homeTeamId == null) throw new FieldException("[homeTeamId] cannot be null");
        if (awayTeamId == null) throw new FieldException("[awayTeamId] cannot be null");
        if (homeTeamId.equals(awayTeamId)) throw new FieldException("[homeTeam] and [awayTeam] cannot be the same");
    }

    private static void validateTeams(Team homeTeam, Team awayTeam) {
        if (!homeTeam.getIsActive()) throw new FieldException("[homeTeam] is not active");
        if (!awayTeam.getIsActive()) throw new FieldException("[awayTeam] is not active");
    }

    private static void validateDateTime(LocalDateTime dateTime, LocalDate homeTeamDate, LocalDate awayTeamDate) {
        if (dateTime == null) throw new FieldException("[matchDateTime] cannot be null");
        if (dateTime.isAfter(LocalDateTime.now())) throw new FieldException("[matchDateTime] cannot be in the future");
        if (dateTime.isBefore(homeTeamDate.atStartOfDay())) throw new ConflictException("[matchDateTime] cannot be before [homeTeamDateCreated]");
        if (dateTime.isBefore(awayTeamDate.atStartOfDay())) throw new ConflictException("[matchDateTime] cannot be before [awayTeamDateCreated]");
    };

    private static void validateConflictMatches(LocalDateTime dateTime, List<Match> matches) {
        matches.forEach(match -> {
            if (Duration.between(dateTime, match.getMatchDateTime()).toHours() < 48) {
                throw new ConflictException("Cannot create a match when one of the teams already has a match in less than 48 hours");
            };
        });
    }
}

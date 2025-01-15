package com.meli.football_matches_api.validations;

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

import java.time.*;
import java.util.List;
import java.util.Objects;

public class MatchValidations {

    public static void validateFields(MatchDTO matchDTO, MatchRepository matchRepository, TeamRepository teamRepository, StadiumRepository stadiumRepository) {
        Long homeTeamId = matchDTO.getHomeTeam().getId();
        Long awayTeamId = matchDTO.getAwayTeam().getId();

        validateIds(homeTeamId, awayTeamId);

        Team homeTeam = teamRepository.findById(homeTeamId);
        if (homeTeam == null) throw new NotFoundException("homeTeam not found");

        Team awayTeam = teamRepository.findById(awayTeamId);
        if (awayTeam == null) throw new NotFoundException("awayTeam not found");

        validateGoals(matchDTO.getHomeGoals(), matchDTO.getAwayGoals());
        validateTeams(homeTeam, awayTeam);

        LocalDateTime matchDateTime = matchDTO.getMatchDateTime();
        validateDateTime(matchDateTime, homeTeam.getDateCreated(), awayTeam.getDateCreated());

        Long matchId = matchDTO.getId();

        List<Match> homeTeamMatches = matchRepository.findAllByHomeTeam(homeTeam);
        validateConflictMatches(matchId, matchDateTime, homeTeamMatches);

        List<Match> awayTeamMatches = matchRepository.findAllByAwayTeam(awayTeam);
        validateConflictMatches(matchId, matchDateTime, awayTeamMatches);

        validateStadium(matchDTO.getStadium(), stadiumRepository, matchDateTime);
    };

    public static void validateIfMatchExists(int matchId, MatchRepository matchRepository) {
        matchRepository.findById(matchId).orElseThrow(() -> new NotFoundException("Match not found"));
    }

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

    private static void validateConflictMatches(Long matchId, LocalDateTime dateTime, List<Match> matches) {
        matches.forEach(match -> {
            if (Math.abs(Duration.between(dateTime, match.getMatchDateTime()).toHours()) < 48 && (!Objects.equals(match.getId(), matchId))) {
                throw new ConflictException("Cannot create a match when one of the teams already has a match in less than 48 hours");
            };
        });
    }

    private static void validateStadium(Stadium stadiumObj, StadiumRepository stadiumRepository, LocalDateTime dateTime) {
        if (stadiumObj == null) throw new FieldException("[stadium] cannot be null");

        Long stadiumId = stadiumObj.getId();
        if (stadiumId == null) throw new FieldException("[stadium.id] cannot be null");

        Stadium stadium = stadiumRepository.findById(stadiumId);
        if (stadium == null) throw new FieldException("Stadium not found");

        List<Match> matches = stadium.getMatches();

        for (Match match : matches) {
            LocalDate newMatchDate = dateTime.toLocalDate();
            LocalDate oldMatchDate = match.getMatchDateTime().toLocalDate();

            if (newMatchDate.equals(oldMatchDate)) throw new ConflictException("Stadium already has a match on this date");
        }
    }
}

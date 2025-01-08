package com.meli.football_matches_api.validations;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.exception.FieldException;

import java.time.LocalDateTime;

public class MatchValidations {

    public static void validateFields(MatchDTO matchDTO) {
        if (matchDTO.getHomeGoals() == null) throw new FieldException("[awayGoals] cannot be null");
        if (matchDTO.getAwayGoals() == null) throw new FieldException("[homeGoals] cannot be null");
        if (matchDTO.getIdStadium() == null) throw new FieldException("[idStadium] cannot be null");
        if (matchDTO.getMatchDateTime() == null) throw new FieldException("[matchDateTime] cannot be null");

        validateTeams(matchDTO.getIdHomeTeam(), matchDTO.getIdAwayTeam());
        validateDateTime(matchDTO.getMatchDateTime());
    };

    private static void validateTeams(Long homeTeamId, Long awayTeamId) {
        if (homeTeamId == null) throw new FieldException("[homeTeamId] cannot be null");
        if (awayTeamId == null) throw new FieldException("[awayTeamId] cannot be null");
        if (homeTeamId.equals(awayTeamId)) throw new FieldException("[idHomeTeam] and [awayTeam] cannot be the same");
    }

    private static void validateDateTime(LocalDateTime dateTime) {
        if (dateTime == null) throw new FieldException("[matchDateTime] cannot be null");

        if (dateTime.isAfter(LocalDateTime.now())) throw new FieldException("[matchDateTime] cannot be in the future");
    };
}

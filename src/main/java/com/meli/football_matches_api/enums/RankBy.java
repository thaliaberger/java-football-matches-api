package com.meli.football_matches_api.enums;

import com.meli.football_matches_api.exception.InvalidValueException;

public enum RankBy {
    GOALS,
    SCORE,
    MATCHES,
    WINS;

    public static RankBy convertStringToEnum(String rankBy) {
        try {
            return RankBy.valueOf(rankBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException("Invalid rankBy value: " + rankBy);
        }
    }
}

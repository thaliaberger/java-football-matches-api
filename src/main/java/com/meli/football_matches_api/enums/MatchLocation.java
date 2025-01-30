package com.meli.football_matches_api.enums;

import com.meli.football_matches_api.exception.InvalidValueException;

public enum MatchLocation {
    ALL,
    AWAY,
    HOME;

    public static MatchLocation convertStringToEnum(String matchLocation){
        try {
            return MatchLocation.valueOf(matchLocation.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException("Invalid matchLocation value: " + matchLocation);
        }
    }
}

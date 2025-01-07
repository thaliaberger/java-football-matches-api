package com.meli.football_matches_api.utils;

import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.ITeam;

import java.time.LocalDate;
import java.util.Arrays;

public class Utils {

    private final static String[] STATES = { "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "TO" };

    public static boolean isValidState(String state) {

        return Arrays.asList(STATES).contains(state);
    }

    public static void validateFields(TeamDTO teamDTO) {
        if (teamDTO.getName() == null || teamDTO.getName().isEmpty()) throw new FieldException("Field name cannot be empty");
        if (teamDTO.getIsActive() == null) throw new FieldException("Field isActive cannot be null");

        validateDateCreated(teamDTO.getDateCreated());
        validateState(teamDTO.getState());
    };

    private static void validateDateCreated(LocalDate date) {
        if (date == null) throw new FieldException("dateCreated cannot be null");

        if (date.isAfter(LocalDate.now())) throw new FieldException("dateCreated cannot be in the future");
    };

    private static void validateState(String state) {
        if (state == null || state.isEmpty()) throw new FieldException("Field state cannot be empty");

        if (state.length() != 2) throw new FieldException("Field state must contain 2 characters");

        if (!Utils.isValidState(state)) throw new FieldException("state is not a valid");
    };
}

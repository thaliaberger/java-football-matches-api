package com.meli.football_matches_api.validations;

import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.TeamRepository;

import java.time.LocalDate;
import java.util.Arrays;

public class TeamValidations {
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

    public static void validateIfTeamAlreadyExists(String teamName, String state, TeamRepository repository) {
        Team existingTeam = repository.findByNameAndState(teamName, state);
        if (existingTeam != null) throw new ConflictException("Already existing team with name [" + teamName + "] and state [" + state + "]");
    }

    private static void validateDateCreated(LocalDate date) {
        if (date == null) throw new FieldException("dateCreated cannot be null");

        if (date.isAfter(LocalDate.now())) throw new FieldException("dateCreated cannot be in the future");
    };

    private static void validateState(String state) {
        if (state == null || state.isEmpty()) throw new FieldException("Field state cannot be empty");

        if (state.length() != 2) throw new FieldException("Field state must contain 2 characters");

        if (!isValidState(state)) throw new FieldException("state is not a valid");
    };

}

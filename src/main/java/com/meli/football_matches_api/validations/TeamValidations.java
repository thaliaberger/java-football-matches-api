package com.meli.football_matches_api.validations;

import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.TeamRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

public class TeamValidations {
    private final static String[] STATES = { "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "TO" };

    public static boolean isValidState(String state) {
        return Arrays.asList(STATES).contains(state);
    }

    public static void validateFields(TeamDTO teamDTO, TeamRepository teamRepository, boolean isUpdate) {
        if (teamDTO.getName() == null || teamDTO.getName().isEmpty()) throw new FieldException("[name] cannot be empty or null");
        if (teamDTO.getIsActive() == null) throw new FieldException("[isActive] cannot be null");

        validateIfTeamAlreadyExists(teamDTO.getId(), teamDTO.getName(), teamDTO.getState(), teamRepository, isUpdate);

        validateDateCreated(teamDTO.getDateCreated(), teamRepository, teamDTO.getId(), isUpdate);
        validateState(teamDTO.getState());
    };

    private static void validateIfTeamAlreadyExists(Long id, String teamName, String state, TeamRepository teamRepository, boolean isUpdate) {
        if (isUpdate) {
            ensureTeamExists(id, teamRepository);
        } else {
            ensureTeamDoesNotExist(teamName, state, id, teamRepository);
        }
    }

    private static void ensureTeamExists(Long id, TeamRepository teamRepository) {
        if (!teamRepository.existsById(id)) throw new NotFoundException("Team not found");
    }

    private static void ensureTeamDoesNotExist(String teamName, String state, Long id, TeamRepository teamRepository) {
        Team existingTeam = teamRepository.findByNameAndStateAndIdNot(teamName, state, id);
        if (existingTeam != null) throw new ConflictException("Already existing team with name [" + teamName + "] and state [" + state + "]");
    }

    private static void validateDateCreated(LocalDate date, TeamRepository teamRepository, Long id, boolean isUpdate) {
        if (date == null) throw new FieldException("[dateCreated] cannot be null");

        if (date.isAfter(LocalDate.now())) throw new FieldException("[dateCreated] cannot be in the future");

        LocalDateTime localDateTime = date.atTime(0, 0);
        boolean hasMatchesBeforeDate = teamRepository.existsByIdAndAwayMatchesMatchDateTimeBefore(id, localDateTime) || teamRepository.existsByIdAndHomeMatchesMatchDateTimeBefore(id, localDateTime);
        if (isUpdate && hasMatchesBeforeDate) throw new ConflictException("[dateCreated] cannot be after match date");
    };

    private static void validateState(String state) {
        if (state == null || state.isEmpty()) throw new FieldException("[state] cannot be empty or null");

        if (state.length() != 2) throw new FieldException("[state] must contain 2 characters");

        if (!isValidState(state)) throw new FieldException("[state] is not a valid");
    };

}

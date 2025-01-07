package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.ITeam;
import com.meli.football_matches_api.utils.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    private final ITeam repository;

    public TeamService(ITeam repository) {
        this.repository = repository;
    };

    public ResponseEntity<TeamDTO> create(TeamDTO teamDTO) {
        validateFields(teamDTO);

        validateIfTeamAlreadyExists(teamDTO.getName(), teamDTO.getState());

        Team newTeam = new Team(teamDTO);
        TeamDTO savedTeam = new TeamDTO(repository.save(newTeam));
        return ResponseEntity.status(201).body(savedTeam);
    };

    public ResponseEntity<TeamDTO> update(TeamDTO teamDTO) {
        repository.findById(teamDTO.getId()).orElseThrow(() -> new NotFoundException("Team not found"));

        validateFields(teamDTO);
        validateIfTeamAlreadyExists(teamDTO.getName(), teamDTO.getState());

        Team updatedTeam = new Team(teamDTO);
        TeamDTO savedTeam = new TeamDTO(repository.save(updatedTeam));
        return ResponseEntity.status(200).body(savedTeam);
    }

    private void validateFields(TeamDTO teamDTO) {
        if (teamDTO.getName() == null || teamDTO.getName().isEmpty()) throw new FieldException("Field name cannot be empty");
        if (teamDTO.getIsActive() == null) throw new FieldException("Field isActive cannot be null");

        validateDateCreated(teamDTO.getDateCreated());
        validateState(teamDTO.getState());
    };

    private void validateDateCreated(LocalDate date) {
        if (date == null) throw new FieldException("dateCreated cannot be null");

        if (date.isAfter(LocalDate.now())) throw new FieldException("dateCreated cannot be in the future");
    };

    private void validateState(String state) {
        if (state == null || state.isEmpty()) throw new FieldException("Field state cannot be empty");

        if (state.length() != 2) throw new FieldException("Field state must contain 2 characters");

        if (!Utils.isValidState(state)) throw new FieldException("state is not a valid");
    };

    private void validateIfTeamAlreadyExists(String teamName, String state) {
        Team existingTeam = repository.findByNameAndState(teamName, state);

        if (existingTeam != null) throw new ConflictException("Already existing team with name [" + teamName + "] and state [" + state + "]");
    }
}

package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.exception.ConflictException;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.ITeam;
import com.meli.football_matches_api.utils.Utils;
import com.meli.football_matches_api.validations.Validations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final ITeam repository;

    public TeamService(ITeam repository) {
        this.repository = repository;
    };

    public ResponseEntity<TeamDTO> create(TeamDTO teamDTO) {
        Validations.validateFields(teamDTO);
        validateIfTeamAlreadyExists(teamDTO.getName(), teamDTO.getState());

        Team newTeam = new Team(teamDTO);
        TeamDTO savedTeam = new TeamDTO(repository.save(newTeam));
        return ResponseEntity.status(201).body(savedTeam);
    };

    public ResponseEntity<TeamDTO> update(TeamDTO teamDTO) {
        repository.findById(teamDTO.getId()).orElseThrow(() -> new NotFoundException("Team not found"));

        Validations.validateFields(teamDTO);
        validateIfTeamAlreadyExists(teamDTO.getName(), teamDTO.getState());

        Team updatedTeam = new Team(teamDTO);
        TeamDTO savedTeam = new TeamDTO(repository.save(updatedTeam));
        return ResponseEntity.status(200).body(savedTeam);
    }

    public ResponseEntity<TeamDTO> get(int id) {
        Team team = repository.findById(id);
        if (team == null) throw new NotFoundException("Team not found");

        TeamDTO teamDTO = new TeamDTO(team);
        return ResponseEntity.status(200).body(teamDTO);
    }

    public ResponseEntity<List<TeamDTO>> list() {
        List<Team> teams = repository.findAll();
        return ResponseEntity.status(200).body(Utils.convertToDTO(teams));
    }

    public ResponseEntity<List<TeamDTO>> list(String param, Boolean isNameSearch) {
        List<Team> teams;

        if (isNameSearch) {
            teams = repository.findAllByName(param);
        } else {
            teams = repository.findAllByState(param);
        }
        return ResponseEntity.status(200).body(Utils.convertToDTO(teams));
    }

    public ResponseEntity<List<TeamDTO>> list(Boolean isActive) {
        List<Team> teams = repository.findAllByIsActive(isActive);
        return ResponseEntity.status(200).body(Utils.convertToDTO(teams));
    }

    private void validateIfTeamAlreadyExists(String teamName, String state) {
        Team existingTeam = repository.findByNameAndState(teamName, state);

        if (existingTeam != null) throw new ConflictException("Already existing team with name [" + teamName + "] and state [" + state + "]");
    }
}

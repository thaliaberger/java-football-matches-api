package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.exception.FieldException;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.ITeam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TeamService {

    private final ITeam repository;

    public TeamService(ITeam repository) {
        this.repository = repository;
    };

    public ResponseEntity<TeamDTO> create(TeamDTO teamDTO) {
        validateFields(teamDTO);
        Team newTeam = new Team(teamDTO);
        TeamDTO savedTeam = new TeamDTO(repository.save(newTeam));
        return ResponseEntity.status(201).body(savedTeam);
    };

    private void validateFields(TeamDTO teamDTO) {
        validateDateCreated(teamDTO.getDateCreated());
    }

    private void validateDateCreated(LocalDate date) {
        if (date == null) {
            throw new FieldException("dateCreated cannot be null");
        }

        if (date.isAfter(LocalDate.now())) {
            throw new FieldException("dateCreated cannot be in the future");
        }
    }
}

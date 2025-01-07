package com.meli.football_matches_api.service;

import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.ITeam;
import com.meli.football_matches_api.utils.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class Teste {

    private ITeam repository;

    public Teste(ITeam repository) {
        this.repository = repository;
    }

    public ResponseEntity<Team> create(Team team) {
        return ResponseEntity.status(400).build();
    };

    private String validateFieldsLength(Team team) {
        if (team.getName() == null || team.getName().isEmpty()) {
            return "Team name cannot be empty";
        }

        if (team.getIsActive() == null) {
            return "Team isActive field cannot be null";
        }

        return "";
    }

    private String validateDateCreated(LocalDate date) {
        if (date == null) {
            return "Team dateCreated cannot be null";
        }

        if (date.isAfter(LocalDate.now())) {
            return "dateCreated cannot be in the future";
        }

        return "";
    }

    private String validateState(String state) {
        if (state == null || state.isEmpty()) {
            return "Team state cannot be empty";
        }

        if (state.length() != 2) {
            return "Team state must contain 2 characters";
        }


        if (!Utils.validateState(state)) {
            return "Team state is not a valid state";
        }

        return "";
    }

}

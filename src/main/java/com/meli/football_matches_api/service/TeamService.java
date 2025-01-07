package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.model.Team;
import com.meli.football_matches_api.repository.ITeam;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private final ITeam repository;

    public TeamService(ITeam repository) {
        this.repository = repository;
    };

    public void create(TeamDTO teamDTO) {
        Team newTeam = new Team(teamDTO);
        repository.save(newTeam);
    };
}

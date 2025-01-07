package com.meli.football_matches_api.controller;

import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.service.TeamService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team")
public class TeamController {

    private TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public void create(@RequestBody TeamDTO team) {
        teamService.create(team);
    }
}

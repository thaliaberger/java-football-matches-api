package com.meli.football_matches_api.controller;

import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<TeamDTO> create(@RequestBody TeamDTO team) {
        return teamService.create(team);
    }

    @PutMapping
    public ResponseEntity<TeamDTO> update(@RequestBody TeamDTO team) {
        return teamService.update(team);
    }

    @GetMapping
    public ResponseEntity<TeamDTO> get(@RequestParam int id) {
        return teamService.get(id);
    }

    @GetMapping("/list")
    public ResponseEntity<List<TeamDTO>> list() {
        return teamService.list();
    }

    @GetMapping(value = "/list", params = "name")
    public ResponseEntity<List<TeamDTO>> listByName(@RequestParam String name) {
        return teamService.list(name, true);
    }

    @GetMapping(value = "/list", params = "state")
    public ResponseEntity<List<TeamDTO>> listByState(@RequestParam String state) {
        return teamService.list(state, false);
    }

    @GetMapping(value = "/list", params = "isActive")
    public ResponseEntity<List<TeamDTO>> list(@RequestParam Boolean isActive) {
        return teamService.list(isActive);
    }
}

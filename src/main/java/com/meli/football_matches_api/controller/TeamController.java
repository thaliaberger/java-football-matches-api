package com.meli.football_matches_api.controller;

import com.meli.football_matches_api.DTO.RetrospectDTO;
import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

@RestController
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

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
    public ResponseEntity<TeamDTO> get(@RequestParam Long id) {
        return teamService.get(id);
    }

    @GetMapping("/list")
    public ResponseEntity<List<TeamDTO>> list(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "itemsPerPage", required = false, defaultValue = "5") Integer itemsPerPage,
            @RequestParam(name = "sort", required = false, defaultValue = "id,asc") String sort,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean isActive
    ) {
        if (name != null) {
            return teamService.list(page, itemsPerPage, sort, name, true);
        } else if (state != null) {
            return teamService.list(page, itemsPerPage, sort, state, false);
        } else if (isActive != null) {
            return teamService.list(page, itemsPerPage, sort, isActive);
        } else {
            return teamService.list(page, itemsPerPage, sort);
        }
    }

    @DeleteMapping(params = "id")
    public ResponseEntity<String> delete(@RequestParam Long id) {
        return teamService.delete(id);
    }

    @GetMapping(value = "/retrospect", params = "id")
    public ResponseEntity<RetrospectDTO> retrospect(
            @RequestParam Long id,
            @RequestParam(required = false) Long opponentId,
            @RequestParam(required = false, defaultValue="") String matchLocation,
            @RequestParam(required = false, defaultValue="false") boolean isHammering
    ) {
        if (opponentId != null) return teamService.getRetrospect(id, opponentId, matchLocation, isHammering);
        return teamService.getRetrospect(id, matchLocation, isHammering);
    }

    @GetMapping(value = "/retrospect/all", params = "id")
    public ResponseEntity<HashMap<String, RetrospectDTO>> retrospectAll(@RequestParam Long id) {
        return teamService.getRetrospectAgainstAll(id);
    }

    @GetMapping(value = "/ranking", params = "rankBy")
    public ResponseEntity<List<TeamDTO>> ranking(
            @RequestParam String rankBy,
            @RequestParam(required = false) String matchLocation
    ) {
        if (matchLocation != null) return teamService.ranking(rankBy, matchLocation);
        return teamService.ranking(rankBy);
    }
}

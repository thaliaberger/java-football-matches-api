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
    public ResponseEntity<TeamDTO> get(@RequestParam Long id) {
        return teamService.get(id);
    }

    @GetMapping("/list")
    public ResponseEntity<List<TeamDTO>> list() {
        return teamService.list();
    }

    @GetMapping(value = "/list", params = "page")
    public ResponseEntity<List<TeamDTO>> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "itemsPerPage", defaultValue = "5") int itemsPerPage,
            @RequestParam(name = "sort", defaultValue="id") String sort
    ) {
        return teamService.list(page, itemsPerPage, sort);
    }

    @GetMapping(value = "/list", params = "sort")
    public ResponseEntity<List<TeamDTO>> list(@RequestParam(name = "sort", defaultValue="id") String sort) {
        return teamService.list(sort);
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

    @DeleteMapping(params = "id")
    public ResponseEntity<String> delete(@RequestParam Long id) {
        return teamService.delete(id);
    }

    @GetMapping(value = "/retrospect", params = "id")
    public ResponseEntity<RetrospectDTO> retrospect(@RequestParam Long id) {
        return teamService.getRetrospect(id);
    }

    @GetMapping(value = "/retrospect", params = { "id", "matchLocation" })
    public ResponseEntity<RetrospectDTO> retrospect(
            @RequestParam Long id,
            @RequestParam String matchLocation
    ) {
        return teamService.getRetrospect(id, matchLocation);
    }

    @GetMapping(value = "/retrospect/all", params = "id")
    public ResponseEntity<HashMap<String, RetrospectDTO>> retrospectAll(@RequestParam Long id) {
        return teamService.getRetrospectAgainstAll(id);
    }

    @GetMapping(value = "/retrospect", params = { "id" , "opponentId" })
    public ResponseEntity<RetrospectDTO> retrospect(@RequestParam Long id, @RequestParam Long opponentId) {
        return teamService.getRetrospect(id, opponentId);
    }

    @GetMapping(value = "/retrospect", params = { "id" , "opponentId", "hammering" })
    public ResponseEntity<RetrospectDTO> retrospect(
            @RequestParam Long id,
            @RequestParam Long opponentId,
            @RequestParam(name = "hammering", defaultValue="true") boolean isHammering
    ) {
        return teamService.getRetrospect(id, opponentId, isHammering);
    }

    @GetMapping(value = "/ranking", params = "rankBy" )
    public ResponseEntity<PriorityQueue<TeamDTO>> ranking(@RequestParam String rankBy) {
        return teamService.ranking(rankBy);
    }

    @GetMapping(value = "/ranking", params = { "rankBy", "matchLocation" } )
    public ResponseEntity<PriorityQueue<TeamDTO>> ranking(
            @RequestParam String rankBy,
            @RequestParam String matchLocation
    ) {
        return teamService.ranking(rankBy, matchLocation);
    }
}

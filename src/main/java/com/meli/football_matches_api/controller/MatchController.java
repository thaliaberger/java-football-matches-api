package com.meli.football_matches_api.controller;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public ResponseEntity<MatchDTO> create(@RequestBody MatchDTO match) {
        return matchService.create(match);
    }

    @PutMapping
    public ResponseEntity<MatchDTO> update(@RequestBody MatchDTO match) {
        return matchService.update(match);
    }

    @DeleteMapping(params = "id")
    public ResponseEntity<String> delete(@RequestParam Long id) {
        return matchService.delete(id);
    }

    @GetMapping
    public ResponseEntity<MatchDTO> get(@RequestParam Long id) {
        return matchService.get(id);
    }

    @GetMapping("/list")
    public ResponseEntity<List<MatchDTO>> list() {
        return matchService.list();
    }

    @GetMapping(value ="/list", params = "teamId")
    public ResponseEntity<List<MatchDTO>> listByTeam(@RequestParam(name = "teamId") Long teamId) {
        return matchService.listByTeam(teamId);
    }

    @GetMapping(value ="/list", params = { "teamId", "matchLocation" })
    public ResponseEntity<List<MatchDTO>> listByTeam(
            @RequestParam(name = "teamId") Long teamId,
            @RequestParam String matchLocation
    ) {
        return matchService.listByTeamAndMatchLocation(teamId, matchLocation);
    }

    @GetMapping(value ="/list", params = "stadiumId")
    public ResponseEntity<List<MatchDTO>> listByStadium(@RequestParam(name = "stadiumId") Long stadiumId) {
        return matchService.listByStadium(stadiumId);
    }

    @GetMapping(value = "/list", params = "sort")
    public ResponseEntity<List<MatchDTO>> list(@RequestParam(name = "sort", defaultValue="id,asc") String sort) {
        return matchService.list(sort);
    }

    @GetMapping(value = "/list", params = "hammering")
    public ResponseEntity<List<MatchDTO>> list(@RequestParam(name = "hammering", defaultValue="true") Boolean isHammering) {
        return matchService.list(isHammering);
    }

    @GetMapping(value = "/list", params = "page")
    public ResponseEntity<List<MatchDTO>> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "itemsPerPage", defaultValue = "5") int itemsPerPage,
            @RequestParam(name = "sort", defaultValue="id,asc") String sort
    ) {
        return matchService.list(page, itemsPerPage, sort);
    }
}
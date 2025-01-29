package com.meli.football_matches_api.controller;

import com.meli.football_matches_api.DTO.RetrospectDTO;
import com.meli.football_matches_api.DTO.TeamDTO;
import com.meli.football_matches_api.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<TeamDTO> create(@RequestBody TeamDTO team) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.create(team));
    }

    @PutMapping
    public ResponseEntity<TeamDTO> update(@RequestBody TeamDTO team) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.update(team));
    }

    @GetMapping
    public ResponseEntity<TeamDTO> get(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.get(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<TeamDTO>> list(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "1000") Integer itemsPerPage,
            @RequestParam(required = false, defaultValue = "id,asc") String sort,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean isActive
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.list(page, itemsPerPage, sort, name, state, isActive));
    }

    @DeleteMapping(params = "id")
    public ResponseEntity<String> delete(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(teamService.delete(id));
    }

    @GetMapping(value = "/retrospect", params = "id")
    public ResponseEntity<RetrospectDTO> retrospect(
            @RequestParam Long id,
            @RequestParam(required = false) Long opponentId,
            @RequestParam(required = false, defaultValue="") String matchLocation,
            @RequestParam(required = false, defaultValue="false") boolean isHammering
    ) {
        if (opponentId != null) return ResponseEntity.status(HttpStatus.OK).body(teamService.getRetrospect(id, opponentId, matchLocation, isHammering));
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getRetrospect(id, matchLocation, isHammering));
    }

    @GetMapping(value = "/retrospect/all", params = "id")
    public ResponseEntity<Map<String, RetrospectDTO>> retrospectAll(@RequestParam Long id) {
        return  ResponseEntity.status(HttpStatus.OK).body(teamService.getRetrospectAgainstAll(id));
    }

    @GetMapping(value = "/ranking", params = "rankBy")
    public ResponseEntity<List<TeamDTO>> ranking(
            @RequestParam String rankBy,
            @RequestParam(required = false) String matchLocation
    ) {
         return ResponseEntity.status(HttpStatus.OK).body(teamService.ranking(rankBy, matchLocation));
    }
}

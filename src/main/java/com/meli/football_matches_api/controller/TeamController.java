package com.meli.football_matches_api.controller;

import com.meli.football_matches_api.dto.RetrospectDTO;
import com.meli.football_matches_api.dto.TeamDTO;
import com.meli.football_matches_api.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/team")
@Tag(name = "Team")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Operation(
            description = "Create new Team",
            summary = "Create new Team"
    )
    @PostMapping
    public ResponseEntity<TeamDTO> create(@RequestBody TeamDTO team) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.create(team));
    }

    @Operation(
            description = "Update an existing Team",
            summary = "Update an existing Team"
    )
    @PutMapping
    public ResponseEntity<TeamDTO> update(@RequestBody TeamDTO team) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.update(team));
    }

    @Operation(
            description = "Get specific Team by id",
            summary = "Get specific Team by id"
    )
    @GetMapping
    public ResponseEntity<TeamDTO> get(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.get(id));
    }

    @Operation(
            description = "List all Teams",
            summary = "List all Teams"
    )
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

    @Operation(
            description = "Delete specific Team by id",
            summary = "Delete specific Team by id"
    )
    @DeleteMapping(params = "id")
    public ResponseEntity<String> delete(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(teamService.delete(id));
    }

    @Operation(
            description = "Get retrospect of specific Team by id",
            summary = "Get retrospect of specific Team by id"
    )
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

    @Operation(
            description = "Get retrospect of specific Team against all Teams",
            summary = "Get retrospect of specific Team against all Teams"
    )
    @GetMapping(value = "/retrospect/all", params = "id")
    public ResponseEntity<Map<String, RetrospectDTO>> retrospectAll(@RequestParam Long id) {
        return  ResponseEntity.status(HttpStatus.OK).body(teamService.getRetrospectAgainstAll(id));
    }

    @Operation(
            description = "Get Teams ranking",
            summary = "Get Teams ranking by matches, goals, wins or score."
    )
    @GetMapping(value = "/ranking", params = "rankBy")
    public ResponseEntity<List<TeamDTO>> ranking(
            @RequestParam String rankBy,
            @RequestParam(required = false) String matchLocation
    ) {
         return ResponseEntity.status(HttpStatus.OK).body(teamService.ranking(rankBy, matchLocation));
    }
}

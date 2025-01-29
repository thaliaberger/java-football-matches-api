package com.meli.football_matches_api.controller;

import com.meli.football_matches_api.dto.MatchDTO;
import com.meli.football_matches_api.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/match")
@Tag(name = "Match")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @Operation(
            description = "Create a new Match",
            summary = "Create a new Match"
    )
    @PostMapping
    public ResponseEntity<MatchDTO> create(@RequestBody MatchDTO match) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.create(match));
    }

    @Operation(
            description = "Update an existing Match",
            summary = "Update an existing Match"
    )
    @PutMapping
    public ResponseEntity<MatchDTO> update(@RequestBody MatchDTO match) {
        return ResponseEntity.status(HttpStatus.OK).body(matchService.update(match));
    }

    @Operation(
            description = "Delete specific Match by id",
            summary = "Delete specific Match by id"
    )
    @DeleteMapping(params = "id")
    public ResponseEntity<String> delete(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(matchService.delete(id));
    }

    @Operation(
            description = "Get specific Match by id",
            summary = "Get specific Match by id"
    )
    @GetMapping
    public ResponseEntity<MatchDTO> get(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(matchService.get(id));
    }

    @Operation(
            description = "List all Matches",
            summary = "List all Matches"
    )
    @GetMapping(value = "/list")
    public ResponseEntity<List<MatchDTO>> list(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "1000") int itemsPerPage,
            @RequestParam(required = false, defaultValue="id,asc") String sort,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false, defaultValue="false") Boolean isHammering,
            @RequestParam(required = false) Long stadiumId,
            @RequestParam(required = false, defaultValue="") String matchLocation
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(matchService.list(page, itemsPerPage, sort, teamId, stadiumId, matchLocation, isHammering));
    }
}
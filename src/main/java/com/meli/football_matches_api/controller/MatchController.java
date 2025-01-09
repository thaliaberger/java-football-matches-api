package com.meli.football_matches_api.controller;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.model.Match;
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
    public ResponseEntity<String> delete(@RequestParam int id) {
        return matchService.delete(id);
    }

    @GetMapping
    public ResponseEntity<MatchDTO> get(@RequestParam Long id) {
        return matchService.get(id);
    }

    @GetMapping("/list")
    public List<Match> list() {
        return matchService.list();
    }
}
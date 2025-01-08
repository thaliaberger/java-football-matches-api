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

    @GetMapping("/list")
    public List<Match> list() {
        return matchService.list();
    }
}
package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.repository.MatchRepository;
import com.meli.football_matches_api.repository.TeamRepository;
import com.meli.football_matches_api.validations.MatchValidations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    public MatchService(MatchRepository repository) {
        this.matchRepository = repository;
    };

    public ResponseEntity<MatchDTO> create(MatchDTO matchDTO) {
        MatchValidations.validateFields(matchDTO, matchRepository, teamRepository);
        Match newMatch = new Match(matchDTO);
        MatchDTO savedMatch = new MatchDTO(matchRepository.save(newMatch));
        return ResponseEntity.status(201).body(savedMatch);
    };

    public List<Match> list() {
        return matchRepository.findAll();
    }
}
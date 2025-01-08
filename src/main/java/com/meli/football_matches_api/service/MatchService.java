package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.MatchDTO;
import com.meli.football_matches_api.model.Match;
import com.meli.football_matches_api.repository.IMatch;
import com.meli.football_matches_api.validations.MatchValidations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {

    private final IMatch repository;

    public MatchService(IMatch repository) {
        this.repository = repository;
    };

    public ResponseEntity<MatchDTO> create(MatchDTO matchDTO) {
        MatchValidations.validateFields(matchDTO);
        Match newMatch = new Match(matchDTO);
        MatchDTO savedMatch = new MatchDTO(repository.save(newMatch));
        return ResponseEntity.status(201).body(savedMatch);
    };

    public List<Match> list() {
        return repository.findAll();
    }
}
package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.StadiumDTO;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.repository.StadiumRepository;
import com.meli.football_matches_api.validations.StadiumValidations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StadiumService {

    private final StadiumRepository stadiumRepository;

    public StadiumService(StadiumRepository stadiumRepository) {
        this.stadiumRepository = stadiumRepository;
    };

    public ResponseEntity<StadiumDTO> create(StadiumDTO stadiumDTO) {
        StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository);
        Stadium newStadium = new Stadium(stadiumDTO);
        StadiumDTO savedStadium = new StadiumDTO(stadiumRepository.save(newStadium));
        return ResponseEntity.status(201).body(savedStadium);
    };
}

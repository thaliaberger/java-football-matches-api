package com.meli.football_matches_api.service;

import com.meli.football_matches_api.DTO.StadiumDTO;
import com.meli.football_matches_api.exception.NotFoundException;
import com.meli.football_matches_api.model.Stadium;
import com.meli.football_matches_api.repository.StadiumRepository;
import com.meli.football_matches_api.utils.Utils;
import com.meli.football_matches_api.validations.StadiumValidations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StadiumService {

    private final StadiumRepository stadiumRepository;

    public StadiumService(StadiumRepository stadiumRepository) {
        this.stadiumRepository = stadiumRepository;
    };

    public ResponseEntity<StadiumDTO> create(StadiumDTO stadiumDTO) {
        return createOrUpdate(stadiumDTO, false);
    };

    public ResponseEntity<StadiumDTO> update(StadiumDTO stadiumDTO) {
        StadiumValidations.validateIfStadiumExists(stadiumDTO, stadiumRepository);
        return createOrUpdate(stadiumDTO, true);
    };

    public ResponseEntity<StadiumDTO> createOrUpdate(StadiumDTO stadiumDTO, Boolean isUpdate) {
        StadiumValidations.validateName(stadiumDTO.getName(), stadiumRepository, isUpdate);
        Stadium newStadium = new Stadium(stadiumDTO);
        StadiumDTO savedStadium = new StadiumDTO(stadiumRepository.save(newStadium));
        int statusCode = isUpdate ? 200 : 201;
        return ResponseEntity.status(statusCode).body(savedStadium);
    };

    public ResponseEntity<StadiumDTO> get(Long id) {
        Stadium stadium = stadiumRepository.findById(id);
        if (stadium == null) throw new NotFoundException("Stadium not found");

        StadiumDTO stadiumDTO = new StadiumDTO(stadium);
        return ResponseEntity.status(200).body(stadiumDTO);
    }

    public ResponseEntity<List<StadiumDTO>> list() {
        List<Stadium> stadiums = stadiumRepository.findAll();
        return ResponseEntity.status(200).body(Utils.convertToStadiumDTO(stadiums));
    }

    public ResponseEntity<List<StadiumDTO>> list(String sort) {
        return list(0, 1000, sort);
    }

    public ResponseEntity<List<StadiumDTO>> list(int page, int itemsPerPage, String sort) {
        Pageable pageable = PageRequest.of(page, itemsPerPage, Utils.handleSortParams(sort));
        List<Stadium> stadiums = stadiumRepository.findAll(pageable).getContent();
        return ResponseEntity.status(200).body(Utils.convertToStadiumDTO(stadiums));
    }
}
